cmake_minimum_required(VERSION 3.2.2)
project("diplomovka_gui")
set(CMAKE_CXX_STANDARD 11)

#set(Boost_USE_STATIC_LIBS ON)
#set(Boost_USE_MULTITHREADED ON)
#set(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/cmake)

#find_package(Boost REQUIRED COMPONENTS math_c99)
find_package( OpenCV REQUIRED )
find_package( Caffe REQUIRED )

file( GLOB HEADERS include/*.h)
file( GLOB SOURCES src/*.cpp)

include_directories(
  ${CMAKE_BINARY_DIR}/include
  ${Boost_INCLUDE_DIRS}
  ${Caffe_INCLUDE_DIRS}
  include
)

add_definitions(${Caffe_DEFINITIONS})  


add_executable(
  ${PROJECT_NAME}
  ${HEADERS}
  ${SOURCES}
)

add_library(caffelib SHARED IMPORTED)
set_target_properties(caffelib PROPERTIES IMPORTED_LOCATION /home/martin/innovatrics/image_tools/ext_libs/caffe/build_caffe/lib/libcaffe.so )
#pixman-1

target_link_libraries( ${PROJECT_NAME} ${OpenCV_LIBS} iomp5 caffelib  )
