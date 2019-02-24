# Change log
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### New
- Added a `util` namespace with a `loft` function.
- Added a `flex` namespace with functions that apply their namesakes from the
  `reckon` or `maybe` modules, upon inspection of their arguments.
- Added “maybe” wrappers for `mirror` and `polyhedron`.
- Added a `mean` function.
- Added a `unit-circle-point-coord` function.
- Added some registered Clojure specs for Cartesian coordinates.

## [Version 0.2.0]
### New
- Added a `reckon` module for reasoning about a model.
- Added an `abs` function for absolute values.

## Version 0.1.0
### New
- Added “maybe” wrappers for `rotate`, `scale`, `translate`, `union`,
  `intersection`, `difference` and `polygon`.
- Added a couple of functions for DFM.
- Added threaded fasteners and a script showcasing a selection of these.
- Added `long-hex-diagonal` for computing the long diagonal of a hexagon
  from the short diagonal.
- Added a handful of constants and abbreviations of dubious value:
  √, sin, cos, π and τ.

[Unreleased]: https://github.com/veikman/scad-tarmi/compare/v0.2.0...HEAD
[Version 0.2.0]: https://github.com/veikman/scad-tarmi/compare/v0.1.0...v0.2.0
