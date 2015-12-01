cmake_minimum_required(VERSION 3.2.2)
project("mbio2")
set(CMAKE_CXX_STANDARD 11)

set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_MULTITHREADED ON)
#set(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/cmake)

find_package(Boost REQUIRED COMPONENTS math_c99)
#find_package(Eigen3 REQUIRED)

file( GLOB HEADERS include/*.h)
file( GLOB SOURCES src/*.cpp)
#file( GLOB_RECUIR EIGEN3_SRC src/*.cpp)

include_directories(
  ${CMAKE_BINARY_DIR}/include
  ${Boost_INCLUDE_DIRS}
  include
  #${EIGEN3_INCLUDE_DIR}
)


add_executable(
  ${PROJECT_NAME}
  ${HEADERS}
  ${SOURCES}
  #${EIGEN3_SRC}
)

target_link_libraries( ${PROJECT_NAME} ${Boost_LIBRARIES} )
