# Consumer rules: applied to every app that depends on Blueprint.
# Only keep what R8 cannot trace by itself. Everything an app calls, extends,
# or declares in its manifest or layouts is kept automatically, and libraries
# (Kuper, Frames, ...) bundle their own rules.
# Blueprint reads nothing by reflection, so it needs no rules of its own.
