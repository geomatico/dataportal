Performance tests
==================

JMeter [1]_ can be used to test how the developed web services perform in different scenarios.

Before going on with this document, it is recommended to follow the web test plan
tutorial [2]_, which is very short and easy to read.

JMeter test plans are stored in .jmx files and there are several plans prepared to test the
dataportal services. At the moment of writing, they were on */dataportal/query-core/jmeter*
on the code base.

In order to launch a specific test, it is necessary to:

- Load it into jmeter.
- Set the number of users and the ramp-up period in the "Thread group" element.
- Possibly change the query parameters in the HTTP Request element to adapt to the desired test case.
- Execute Run > Start. 

Note that at any moment it is possible to right click an element in the test plan tree (at left)
and select the *Help* context menu to see a good explanation of the selected element.

Also be careful with the "Save responses to a file" element since it has a linux absolute path. In case a 
non-unix system is used it should be changed to specify the folder and prefix of the files containing
the different query responses. Relative paths are relative to the jmeter working directory.

Referencias
------------

.. [1] http://jmeter.apache.org/
.. [2] http://jmeter.apache.org/usermanual/build-web-test-plan.html