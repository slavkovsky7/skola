# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.5

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /opt/cmake/bin/cmake

# The command to remove a file.
RM = /opt/cmake/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/martin/workspace/skola/diplomovka/diplomovka_gui

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/martin/workspace/skola/diplomovka/diplomovka_gui/build

# Include any dependencies generated for this target.
include CMakeFiles/diplomovka_gui.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/diplomovka_gui.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/diplomovka_gui.dir/flags.make

CMakeFiles/diplomovka_gui.dir/src/main.cpp.o: CMakeFiles/diplomovka_gui.dir/flags.make
CMakeFiles/diplomovka_gui.dir/src/main.cpp.o: ../src/main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/diplomovka_gui.dir/src/main.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/diplomovka_gui.dir/src/main.cpp.o -c /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/main.cpp

CMakeFiles/diplomovka_gui.dir/src/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/diplomovka_gui.dir/src/main.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/main.cpp > CMakeFiles/diplomovka_gui.dir/src/main.cpp.i

CMakeFiles/diplomovka_gui.dir/src/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/diplomovka_gui.dir/src/main.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/main.cpp -o CMakeFiles/diplomovka_gui.dir/src/main.cpp.s

CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.requires:

.PHONY : CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.requires

CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.provides: CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.requires
	$(MAKE) -f CMakeFiles/diplomovka_gui.dir/build.make CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.provides.build
.PHONY : CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.provides

CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.provides.build: CMakeFiles/diplomovka_gui.dir/src/main.cpp.o


CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o: CMakeFiles/diplomovka_gui.dir/flags.make
CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o: ../src/q_normalizer.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o -c /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/q_normalizer.cpp

CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/q_normalizer.cpp > CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.i

CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/q_normalizer.cpp -o CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.s

CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.requires:

.PHONY : CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.requires

CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.provides: CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.requires
	$(MAKE) -f CMakeFiles/diplomovka_gui.dir/build.make CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.provides.build
.PHONY : CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.provides

CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.provides.build: CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o


CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o: CMakeFiles/diplomovka_gui.dir/flags.make
CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o: ../src/helpers.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o -c /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/helpers.cpp

CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/helpers.cpp > CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.i

CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/helpers.cpp -o CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.s

CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.requires:

.PHONY : CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.requires

CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.provides: CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.requires
	$(MAKE) -f CMakeFiles/diplomovka_gui.dir/build.make CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.provides.build
.PHONY : CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.provides

CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.provides.build: CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o


CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o: CMakeFiles/diplomovka_gui.dir/flags.make
CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o: ../src/findEyeCenter.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o -c /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/findEyeCenter.cpp

CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/findEyeCenter.cpp > CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.i

CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/martin/workspace/skola/diplomovka/diplomovka_gui/src/findEyeCenter.cpp -o CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.s

CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.requires:

.PHONY : CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.requires

CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.provides: CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.requires
	$(MAKE) -f CMakeFiles/diplomovka_gui.dir/build.make CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.provides.build
.PHONY : CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.provides

CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.provides.build: CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o


# Object files for target diplomovka_gui
diplomovka_gui_OBJECTS = \
"CMakeFiles/diplomovka_gui.dir/src/main.cpp.o" \
"CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o" \
"CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o" \
"CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o"

# External object files for target diplomovka_gui
diplomovka_gui_EXTERNAL_OBJECTS =

diplomovka_gui: CMakeFiles/diplomovka_gui.dir/src/main.cpp.o
diplomovka_gui: CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o
diplomovka_gui: CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o
diplomovka_gui: CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o
diplomovka_gui: CMakeFiles/diplomovka_gui.dir/build.make
diplomovka_gui: /usr/local/lib/libopencv_videostab.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_superres.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_stitching.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_shape.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_photo.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_objdetect.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_hal.a
diplomovka_gui: /usr/local/lib/libopencv_calib3d.so.3.0.0
diplomovka_gui: /home/martin/innovatrics/image_tools/ext_libs/caffe/build_caffe/lib/libcaffe.so
diplomovka_gui: /usr/local/lib/libopencv_features2d.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_ml.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_highgui.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_videoio.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_imgcodecs.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_flann.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_video.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_imgproc.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_core.so.3.0.0
diplomovka_gui: /usr/local/lib/libopencv_hal.a
diplomovka_gui: /usr/local/share/OpenCV/3rdparty/lib/libippicv.a
diplomovka_gui: CMakeFiles/diplomovka_gui.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Linking CXX executable diplomovka_gui"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/diplomovka_gui.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/diplomovka_gui.dir/build: diplomovka_gui

.PHONY : CMakeFiles/diplomovka_gui.dir/build

CMakeFiles/diplomovka_gui.dir/requires: CMakeFiles/diplomovka_gui.dir/src/main.cpp.o.requires
CMakeFiles/diplomovka_gui.dir/requires: CMakeFiles/diplomovka_gui.dir/src/q_normalizer.cpp.o.requires
CMakeFiles/diplomovka_gui.dir/requires: CMakeFiles/diplomovka_gui.dir/src/helpers.cpp.o.requires
CMakeFiles/diplomovka_gui.dir/requires: CMakeFiles/diplomovka_gui.dir/src/findEyeCenter.cpp.o.requires

.PHONY : CMakeFiles/diplomovka_gui.dir/requires

CMakeFiles/diplomovka_gui.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/diplomovka_gui.dir/cmake_clean.cmake
.PHONY : CMakeFiles/diplomovka_gui.dir/clean

CMakeFiles/diplomovka_gui.dir/depend:
	cd /home/martin/workspace/skola/diplomovka/diplomovka_gui/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/martin/workspace/skola/diplomovka/diplomovka_gui /home/martin/workspace/skola/diplomovka/diplomovka_gui /home/martin/workspace/skola/diplomovka/diplomovka_gui/build /home/martin/workspace/skola/diplomovka/diplomovka_gui/build /home/martin/workspace/skola/diplomovka/diplomovka_gui/build/CMakeFiles/diplomovka_gui.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/diplomovka_gui.dir/depend

