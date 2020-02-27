# Change log
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Changed
- Marked parameter validation specs in `scad-tarmi.threaded` as deprecated.
  Preferred specs are available in `scad-tarmi.threaded.schema`.
- The parameter validator for threaded bolts now requires at least a head type
  in the absence of an explicit total length, so that the length of the bolt can
  always be inferred. This is in addition to prior requirements.

### New
- Added a Boolean `include-threading` parameter to `bolt` and `rod` functions,
  enabling negatives of threaded fasteners with a diameter suited to tapping
  the threading with standard tools instead of printing the threading.
- In addition to its previous behaviour, `maybe/translate` will now absorb
  child elements that are single translations, further simplifying output.
- Added a `total-bolt-length` function to the `threaded` module, for predicting
  the length of a bolt.
- Added a `schema` module for parameters to `threaded` functions, with parsers
  and a new composite spec for the `bolt` function.

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

[Unreleased]: https://github.com/veikman/scad-tarmi/compare/v0.4.0...HEAD
[Version 0.4.0]: https://github.com/veikman/scad-tarmi/compare/v0.3.0...v0.4.0
[Version 0.3.0]: https://github.com/veikman/scad-tarmi/compare/v0.2.0...v0.3.0
[Version 0.2.0]: https://github.com/veikman/scad-tarmi/compare/v0.1.0...v0.2.0
