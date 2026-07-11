# flutter_hbb

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://docs.flutter.dev/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://docs.flutter.dev/cookbook)

For help getting started with Flutter development, view the
[online documentation](https://docs.flutter.dev/), which offers tutorials,
samples and guidance on mobile development, and a full API reference.

## Android terminal background keep-alive

Mobile terminal sessions start a dedicated Android foreground service while a
terminal page is open. The service keeps a visible notification and holds a
partial wake lock so the active terminal socket can continue running after the
app moves to the background or the screen turns off.

The keep-alive service is reference-counted from Dart, so multiple terminal
pages can share it and it stops only after the last terminal page closes. It
does not restore a terminal session after Android or the device vendor kills the
process; users still need to allow notifications, disable battery restrictions,
and permit background network activity for best results.
