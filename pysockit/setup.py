import os
from setuptools import setup

# Utility function to read the README file.
# Used for the long_description.  It's nice, because now 1) we have a top level
# README file and 2) it's easier to type in the README file than to put a raw
# string in below ...
def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

setup(
    name = "Sockit",
    version = "1.0.0",
    author = "Paul Fudal and Fabien Benureau",
    author_email = "paul.fudal@inria.fr",
    description = ("A python implementation of client of the Java socket library"),
    license = "Not Yet Decided.",
    keywords = "socket library",
    url = "nourl.com",
    packages=['sockit'],
    long_description=read('README'),
    classifiers=[],
)