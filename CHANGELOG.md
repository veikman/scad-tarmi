# Change log
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
Nothing yet.

## [Version 0.6.0]
### New
- Added `projection`, `project` and `cut` to `maybe`.

## [Version 0.5.0]
### Changed
- The `threaded` module has been marked as deprecated for all purposes. Use
  the `scad-klupe` library instead.

### New
- In addition to its previous behaviour, `maybe/translate` will now absorb
  child elements that are single translations, further simplifying output.

## [Version 0.4.0]
### New
- Added an optional `point-type` parameter to `threaded/bolt`. It takes one
  value, `:cone`, and leaves the point of the bolt flat when omitted.
- Added a “maybe” wrapper for `hull`.

## [Version 0.3.0]
### New
- Added a `util` namespace with `loft` and `radiate` functions.
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

[Unreleased]: https://github.com/veikman/scad-tarmi/compare/v0.6.0...HEAD
[Version 0.6.0]: https://github.com/veikman/scad-tarmi/compare/v0.5.0...v0.6.0
[Version 0.5.0]: https://github.com/veikman/scad-tarmi/compare/v0.4.0...v0.5.0
[Version 0.4.0]: https://github.com/veikman/scad-tarmi/compare/v0.3.0...v0.4.0
[Version 0.3.0]: https://github.com/veikman/scad-tarmi/compare/v0.2.0...v0.3.0
[Version 0.2.0]: https://github.com/veikman/scad-tarmi/compare/v0.1.0...v0.2.0
