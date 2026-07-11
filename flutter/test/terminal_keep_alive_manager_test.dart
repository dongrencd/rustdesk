import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_hbb/mobile/terminal_keep_alive_manager.dart';

void main() {
  test('starts once and stops after the last terminal key is released',
      () async {
    var starts = 0;
    var stops = 0;
    final manager = TerminalKeepAliveManager(
      start: () async => starts++,
      stop: () async => stops++,
    );
    final firstKey = Object();
    final secondKey = Object();

    await manager.enable(firstKey);
    await manager.enable(firstKey);
    await manager.enable(secondKey);
    await manager.disable(firstKey);

    expect(starts, 1);
    expect(stops, 0);

    await manager.disable(secondKey);
    await manager.disable(secondKey);

    expect(starts, 1);
    expect(stops, 1);
  });
}
