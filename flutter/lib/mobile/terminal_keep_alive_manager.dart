typedef TerminalKeepAliveAction = Future<void> Function();

/// Reference-counts Android terminal keep-alive requests.
///
/// Mobile terminal pages can share the same peer connection. This manager keeps
/// the foreground service running until the last terminal page has released its
/// key, so closing one page does not accidentally stop another active terminal.
class TerminalKeepAliveManager {
  TerminalKeepAliveManager({
    required TerminalKeepAliveAction start,
    required TerminalKeepAliveAction stop,
  })  : _start = start,
        _stop = stop;

  final TerminalKeepAliveAction _start;
  final TerminalKeepAliveAction _stop;
  final Set<Object> _enabledKeys = {};
  bool _isServiceRunning = false;

  Future<void> enable(Object key) async {
    if (_enabledKeys.contains(key)) {
      return;
    }
    _enabledKeys.add(key);
    if (_isServiceRunning) {
      return;
    }
    try {
      await _start();
      _isServiceRunning = true;
    } catch (_) {
      _enabledKeys.remove(key);
      rethrow;
    }
  }

  Future<void> disable(Object key) async {
    if (!_enabledKeys.remove(key) || _enabledKeys.isNotEmpty) {
      return;
    }
    if (!_isServiceRunning) {
      return;
    }
    await _stop();
    _isServiceRunning = false;
  }
}
