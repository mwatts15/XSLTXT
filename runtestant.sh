## Run ant to test the ant extension tasks.

export JAVA_HOME=/usr/local/java/jdk

# We need to add our classes, log4j and the log4j properties file
# to the class path to be able to run the XSLTXTProcess task
export CLASSPATH=$CLASSPATH:classes:lib/log4j.jar:properties

# Test using new xsltxt task
/usr/local/java/jakarta-ant/bin/ant -emacs -v -f build_xsltxt.xml

# Test using style task with processor attribute
/usr/local/java/jakarta-ant/bin/ant -emacs -v -f build_style.xml 