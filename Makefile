# GNU makefile.
# For getting access to lein exec, refer to
# https://github.com/kumarshantanu/lein-exec.

.PHONY: showcase test clean

showcase:
	lein exec -p src/showcase/core.clj

test:
	lein test

clean:
	-rm showcase/scad/*.scad && rmdir showcase/scad/
	lein clean
