# JUnit Helper

## What's this?

"JUnit Helper" helps JUnit users to cover all tests for public, protected and package local methods by saving much labor to start writing new test cases. 

Currently, command line script, Maven plugin and Eclipse IDE plugin are provided. Eclipse plugin also has an advantage of making it easier to go back and forth between developing class and test class by "Alt + 8 "(test->dev) and "Alt + 9"(dev->test).

## JUnit testing without dull routine

We developers have to do two non-essential dull routine in JUnit testing.

First, it takes no small cost to prepare a new test case. A general procedure to prepare a JUnit test case might be like this - (1) Creating package and test class (2) Defining test methods (3) Finding out how to instantiate (4) Preparing required args, (5) Starting writing logic at last.

JUnit Helper automates (1) - (4) of above. Developers can focus on writing logic.

And second, it is a hard job to ascertain whether the test case aimed to add already exists in a huge test class that might not be maintained for a long time. 

JUnit Helper deals with the problem by using a naming convention of test methods that is based on method signature. For example, "public void doSomething(String arg)" -> "@Test public void doSomething_A$String() throws Exception". The naming rule is customizable to meet some needs.

## Overview

If you want to test the following method:

```java
public SearchResult search(SearchCondition condition) {
  return null;
}
```

Carry out whichever operation:

* Command Line Script (for Windows, Mac OS X, UNIX)

```sh
./junithelper(.bat) make src/main/java
```

* Maven

```sh
mvn junithelper:make
```

* Eclipse plugin

Select Java file and "Alt + 9"

JUnit Helper outputs the following test method:

```java
@Test
public void search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  Sample target = new Sample();
  SearchCondition condition = null;
  SearchResult actual = target.search(condition);
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

JUnit 3.x style is also available:

```java
public void test_search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  ...
```

It will be provided without compile errors in most cases. For example, if the args contain primitives or arrays,

```java
public SearchResult search(String[] array, List<String> list, long primitive, Version version) {
  return null;
}
```

like this:

```java
@Test
public void search_A$StringArray$List$long$Version() throws Exception {
  // TODO auto-generated by JUnit Helper.
  Sample target = new Sample();
  String[] array = new String[] {};
  List<String> list = new ArrayList<String>();
  long primitive = 0L;
  Version version = null;
  SearchResult actual = target.search(array, list, primitive, version);
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

"search_A$SearchCondition" or "search_A$StringArray$List$long$Version" is the part to specify which method it tests. Delimiters and some other rules are customizable.

If you want to have several cases for same method, it's a standard way to discriminate them by method name suffix.

```java
@Test
public void search_A$SearchCondition_null() throws Exception {
  // TODO auto-generated by JUnit Helper.
  ...
}

@Test
public void search_A$SearchCondition_empty() throws Exception {
  // TODO auto-generated by JUnit Helper.
  ...
}

@Test
public void search_A$SearchCondition_paging() throws Exception {
  // TODO auto-generated by JUnit Helper.
  ...
}
```

If you add a new method, 

```java
public List<String> addedMethod(InputStream is) {
  return null;
}
```

JUnit Helper also adds a test method by make command or "Alt + 9" on Eclipse:

```java
@Test
public void addedMethod_A$InputStream() throws Exception {
  // TODO auto-generated by JUnit Helper.
  ...
```

## With mock object frameworks

If you want, JUnit Helper is also able to generate template test code with mock object frameworks:

### Mockito

```java
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@Test
public void search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  Sample target = new Sample();
  // given
  SearchCondition condition = mock(SearchCondition.class);
  // e.g. : given(mocked.called()).willReturn(1);
  // when
  SearchResult actual = target.search(condition);
  // then
  // e.g. : verify(mocked).called();
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

### JMock2

```java
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;

@Test
public void search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  Mockery context = new Mockery(){{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};
  Sample target = new Sample();
  final SearchCondition condition = context.mock(SearchCondition.class);
  context.checking(new Expectations(){{
    // e.g. : allowing(mocked).called(); will(returnValue(1));
  }});
  SearchResult actual = target.search(condition);
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

### EasyMock

```java
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;

@Test
public void search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  IMocksControl mocks = EasyMock.createControl();
  Sample target = new Sample();
  SearchCondition condition = mocks.createMock(SearchCondition.class);
  // e.g. : EasyMock.expect(mocked.called()).andReturn(1);
  mocks.replay();
  SearchResult actual = target.search(condition);
  mocks.verify();
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

### JMockit

```java
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import mockit.Mocked;
import mockit.Expectations;

@Mocked 
SearchCondition search_A$SearchCondition_condition;

@Test
public void search_A$SearchCondition() throws Exception {
  // TODO auto-generated by JUnit Helper.
  Sample target = new Sample();
  SearchCondition condition = this.search_A$SearchCondition_condition;
  new Expectations(){{
    // e.g. : mocked.get(anyString); returns(200);
  }};
  SearchResult actual = target.search(condition);
  SearchResult expected = null;
  assertThat(actual, is(equalTo(expected));
}
```

# Eclipse Plugin

## How to install

* Update site

 http://junithelper.org/eclipse/plugins/site.xml

* Eclipse marketplace

 http://marketplace.eclipse.org/content/junit-helper

## How to use

It is very simple, only two shortcut-commands.The following commands are available on Java Editor, Package Explorer and Navigator.

### Commands

* Alt + 8

 Open target class from test when selecting test class, open test target class.

* Alt + 9

![new_test_case](http://junithelper.org/img/new_test_case.png)

 Open test class or create test class if it does not exist when selecting test target class.
 If some methods that have no tests existed, the minimal test methods will be generated automatically.

![junit4](http://junithelper.org/img/junit4.png)

 JUnit 3.x style is also available:

![junit3](http://junithelper.org/img/junit3.png)

* Alt + 3

 Change current test case to JUnit 3.x style.

* Alt + 4

 Change current test case to JUnit 4.x style.

### Preference

* Window > Preferences > JUnit Helper

![preference](http://junithelper.org/img/preferences_1.10.png)

 If you need a project-specific configuration, put "junithelper-config.properties" in the project root directory. It will be put above global configuration in Eclipse.

```
# language:en/ja
language:en
outputFileEncoding:UTF-8
directoryPathOfProductSourceCode:src/main/java
directoryPathOfTestSourceCode:src/test/java
# lineBreakPolicy:forceCRLF/forceLF/forceNewFileCRLF/forceNewFileLF
lineBreakPolicy=forceNewFileCRLF
useSoftTabs=false
softTabSize=4
# junitVersion:version3/version4
junitVersion:version4
testCaseClassNameToExtend:junit.framework.TestCase
isTemplateImplementationRequired:true
target.isAccessorExcluded:true
target.isExceptionPatternRequired:true
target.isPackageLocalMethodRequired:true
target.isProtectedMethodRequired:true
target.isPublicMethodRequired:true
# target.regexpCsvForExclusion:com/.example/..+.autogenerated/..+,com/.example/.NoNeedToTest
target.regexpCsvForExclusion:
testMethodName.isArgsRequired:true
testMethodName.isReturnRequired:false
testMethodName.basicDelimiter:_
testMethodName.argsAreaPrefix:A
testMethodName.argsAreaDelimiter:$
testMethodName.returnAreaPrefix:R
testMethodName.returnAreaDelimiter:$
testMethodName.exceptionAreaPrefix:T
testMethodName.exceptionAreaDelimiter:$
# mockObjectFramework:Mockito/JMock2/JMockit/EasyMock
mockObjectFramework:
# testingPatternExplicitComment:ArrangeActAssert/GivenWhenThen
testingPatternExplicitComment:
isExtensionEnabled:true
extensionConfigXML:junithelper-extension.xml
```

# Command Line Script

## How to install

* Zip archive (junithelper-core-X.X.X.zip)

 https://github.com/seratch/junithelper/downloads

## How to use

```sh
$ ./tools/junithelper.bat
  _
   /   _  ._/_/_/_  /_  _  _
(_//_// // / / //_'//_//_'/
                   /
JUnit Helper version 1.11


Usage:
  junithelper [command] [arg1] [arg2]

Commands:
  junithelper make [baseDir/targetJavaFile]
  junithelper force3 [baseDir/targetJavaFile]
  junithelper force4 [baseDir/targetJavaFile]

JVM Options:
  -Djunithelper.configProperties=[filepath]

$
```

* make command

This command creates test cases or methods for the classes under specified package recursively.

```sh
$ ./tools/junithelper.bat make ./src/main/java/org/junithelper/core/util/
  _
   /   _  ._/_/_/_  /_  _  _
(_//_// // / / //_'//_//_'/
                   /
JUnit Helper version 1.11


  Target: C:/workspace/junithelper-core/./src/main/java/org/junithelper/core/util/IOUtil.java
  Target: C:/workspace/junithelper-core/./src/main/java/org/junithelper/core/util/ObjectUtil.java
  Target: C:/workspace/junithelper-core/./src/main/java/org/junithelper/core/util/PrimitiveTypeUtil.java
  Target: C:/workspace/junithelper-core/./src/main/java/org/junithelper/core/util/Stdout.java
  Target: C:/workspace/junithelper-core/./src/main/java/org/junithelper/core/util/ThreadUtil.java

Are you sure?(y/n)
y
  Modified: C:/workspace/junithelper-core/./src/test/java/org/junithelper/core/util/IOUtilTest.java
  Modified: C:/workspace/junithelper-core/./src/test/java/org/junithelper/core/util/ObjectUtilTest.java
  Modified: C:/workspace/junithelper-core/./src/test/java/org/junithelper/core/util/PrimitiveTypeUtilTest.java
  Created: C:/workspace/junithelper-core/./src/test/java/org/junithelper/core/util/StdoutTest.java
  Modified: C:/workspace/junithelper-core/./src/test/java/org/junithelper/core/util/ThreadUtilTest.java

$
```

* force3 command

This command forces test cases JUnit 3.x style for the classes under specified package recursively.

* force4 command

This command forces test cases JUnit 4.x style for the classes under specified package recursively.

## Configuration

You can change the following Configurations by editing "junithelper-config.properties". Maven plugin's Configuration also uses same property names.

```
# language:en/ja
language:en
outputFileEncoding:UTF-8
directoryPathOfProductSourceCode:src/main/java
directoryPathOfTestSourceCode:src/test/java
# lineBreakPolicy:forceCRLF/forceLF/forceNewFileCRLF/forceNewFileLF
lineBreakPolicy=forceNewFileCRLF
useSoftTabs=false
softTabSize=4
# junitVersion:version3/version4
junitVersion:version4
testCaseClassNameToExtend:junit.framework.TestCase
isTemplateImplementationRequired:true
target.isAccessorExcluded:true
target.isExceptionPatternRequired:true
target.isPackageLocalMethodRequired:true
target.isProtectedMethodRequired:true
target.isPublicMethodRequired:true
# target.regexpCsvForExclusion:com/.example/..+.autogenerated/..+,com/.example/.NoNeedToTest
target.regexpCsvForExclusion:
testMethodName.isArgsRequired:true
testMethodName.isReturnRequired:false
testMethodName.basicDelimiter:_
testMethodName.argsAreaPrefix:A
testMethodName.argsAreaDelimiter:$
testMethodName.returnAreaPrefix:R
testMethodName.returnAreaDelimiter:$
testMethodName.exceptionAreaPrefix:T
testMethodName.exceptionAreaDelimiter:$
# mockObjectFramework:Mockito/JMock2/JMockit/EasyMock
mockObjectFramework:
# testingPatternExplicitComment:ArrangeActAssert/GivenWhenThen
testingPatternExplicitComment:
isExtensionEnabled:true
extensionConfigXML:junithelper-extension.xml
```

# Maven Plugin

## How to install

 * pom.xml

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>org.junithelper</groupId>
            <artifactId>maven-junithelper-plugin</artifactId>
        </plugin>
        ...
    </plugins>
</build>
```

Following is an example of configuration:

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>org.junithelper</groupId>
            <artifactId>maven-junithelper-plugin</artifactId>
            <configuration>
                <!-- language:en/ja -->
                <language>en</language>
                <outputFileEncoding>UTF-8</outputFileEncoding>
                <!-- lineBreakPolicy:forceCRLF/forceLF/forceNewFileCRLF/forceNewFileLF -->
                <lineBreakPolicy>forceNewFileCRLF</lineBreakPolicy>
                <useSoftTabs>false</useSoftTabs>
                <softTabSize>4</softTabSize>
                <directoryPathOfProductSourceCode>src/main/java</directoryPathOfProductSourceCode>
                <directoryPathOfTestSourceCode>src/test/java</directoryPathOfTestSourceCode>
                <!-- junitVersion:version3/version4 -->
                <junitVersion>version4</junitVersion>
                <testCaseClassNameToExtend>junit.framework.TestCase</testCaseClassNameToExtend>
                <isTemplateImplementationRequired>true</isTemplateImplementationRequired>
                <target_isAccessorExcluded>true</target_isAccessorExcluded>
                <target_isExceptionPatternRequired>true</target_isExceptionPatternRequired>
                <target_isPackageLocalMethodRequired>true</target_isPackageLocalMethodRequired>
                <target_isProtectedMethodRequired>true</target_isProtectedMethodRequired>
                <target_isPublicMethodRequired>true</target_isPublicMethodRequired>
                <target_regexpCsvForExclusion></target_regexpCsvForExclusion>
                <testMethodName_isArgsRequired>true</testMethodName_isArgsRequired>
                <testMethodName_isReturnRequired>false</testMethodName_isReturnRequired>
                <testMethodName_basicDelimiter>_</testMethodName_basicDelimiter>
                <testMethodName_argsAreaPrefix>A</testMethodName_argsAreaPrefix>
                <testMethodName_argsAreaDelimiter>$</testMethodName_argsAreaDelimiter>
                <testMethodName_returnAreaPrefix>R</testMethodName_returnAreaPrefix>
                <testMethodName_returnAreaDelimiter>$</testMethodName_returnAreaDelimiter>
                <testMethodName_exceptionAreaPrefix>T</testMethodName_exceptionAreaPrefix>
                <testMethodName_exceptionAreaDelimiter>$</testMethodName_exceptionAreaDelimiter>
                <!-- mockObjectFramework:Mockito/JMock2/JMockit/EasyMock -->
                <mockObjectFramework></mockObjectFramework>
                <!-- testingPatternExplicitComment:ArrangeActAssert/GivenWhenThen -->
                <testingPatternExplicitComment></testingPatternExplicitComment>
                <isExtensionEnabled>true</isExtensionEnabled>
                <extensionConfigXML>junithelper-extension.xml</extensionConfigXML>
            </configuration>
        </plugin>
        ...
    </plugins>
</build>
```

## How to use

### Goals

* `junithelper:make` 

This goal is used to execute adding or updating tests. It is possible to specify targets by adding "target" option, for example "mvn junithelper:make -Dtarget=src/main/java/snippet/" or "mvn junithelper:make -Dtarget=src/main/java/snippet/Sample.java".

* `junithepler:force3` 

This goal is used to execute converting tests to JUnit 3.x style. "-Dtarget=***" is also availale.

* `junithelper:force4` 

This goal is used to execute converting tests to JUnit 4.x style.  "-Dtarget=***" is also availale.

```sh
$ mvn junithelper:make
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Building maven-junithelper-plugin Maven Mojo
[INFO]    task-segment: [junithelper:make]
[INFO] ------------------------------------------------------------------------

[INFO] [junithelper:make {execution: default-cli}]
  _
   /   _  ._/_/_/_  /_  _  _
(_//_// // / / //_'//_//_'/
                   /
JUnit Helper version 1.11


  Target: /Users/seratch/IdeaProjects/sample/src/main/java/sample/SampleBean.java

Are you sure?(y/n)

```

# JUnit Helper Extension

## What's this? 

You can customize JUnit Helper's template generation.

## How to use 

* Eclipse Plugin

Put junithelper-extension.xml in the project root directory. If you also put junithelper-config.properties, it is required to change "isExtensionEnabled" to true.

* Command Line Script

Change "isExtensionEnabled" to true in junithelper-config.properties and edit junithelper-extension.xml.

* Maven plugin

Add configuration in pom.xml and put junithelper-extension.xml in the project root directory.

```xml
<plugin>
    <groupId>org.junithelper</groupId>
    <artifactId>maven-junithelper-plugin</artifactId>
    <configuration>
        <isExtensionEnabled>true</isExtensionEnabled>
        <extensionConfigXML>junithelper-extension.xml</extensionConfigXML>
    </configuration>
</plugin>
```

## junithelper-extension.xml 

* instantiation

Customize the default way to instantiate.

* arg

Add customized parameter-range testing patterns to each type of argument.

* return

Add customized assertions to each type of return value.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<junithelper-extension>

  <!-- 
    =============================
     * Instantiation *
    =============================
    [NOTE] Mock objects or extension's Arg Patterns are given preference.
    [NOTE] {instance} will be replaced to arg variable

    ****** <XML> ******
      <instantiation class="com.example.Worker">
        <import>com.example.WorkerFactory</import>
        <assign>WorkerFactory.getNewWorker()</assign>
      </instantiation>
      <instantiation class="java.util.Calendar">
        <assign>Calendar.getInstance()</assign>
        <post-assign>{instance}.add(Calendar.DATE, -1);</post-assign>
      </instantiation>

    ****** <Target> ******
      public void putCalendar(Calendar cal) {}

    ****** <Test> ******
      @Test 
      public void putCalendar_A$() throws Exception {
        // TODO auto-generated by JUnit Helper.
        Worker target = WorkerFactory.getNewWorker();
        Calendar cal = Calendar.getIntance();
        cal.add(Calendar.DATE, -1);
        target.putCalendar(cal);
      }
  -->
  <instantiation class="java.util.Calendar">
    <assign>Calendar.getInstance()</assign>
    <post-assign>{instance}.add(Calendar.DATE, -1)</post-assign>
  </instantiation>
  <instantiation class="java.io.InputStream">
    <import>java.io.ByteArrayInputStream</import>
    <assign>new ByteArrayInputStream(new byte[] {})</assign>
  </instantiation>

  <!-- 
    =============================
     * Arg Patterns *
    =============================
    [NOTE] {arg} will be replaced to arg variable

    ****** <XML> ******
      <arg class="int" >
        <pattern name="minus1"><assign>-1</assign></pattern>
        <pattern name="random">
          <import>java.util.Random</import>
          <pre-assign>System.out.println("Before");</pre-assign>
          <assign>new Random().nextInt(10)</assign>
          <post-assign>System.out.println("After");</post-assign>
        </pattern>
      </arg>

    ****** <Target> ******
      public void increment(int i) {}

    ****** <Test> ******
      @Test 
      public void increment_A$int_intIsMinus1() throws Exception {
        // TODO auto-generated by JUnit Helper.
        Sample target = new Sample();
        int i = -1;
        target.increment(i);
      }
      @Test 
      public void increment_A$int_intIsRandom() throws Exception {
        // TODO auto-generated by JUnit Helper.
        Sample target = new Sample();
        System.out.println("Before");
        int i = new Random().nextInt(10);
        System.out.println("After");
        target.increment(i);
      }
  -->
  <!-- Primitive types -->
  <arg class="int" >
    <pattern name="minus1"><assign>-1</assign></pattern>
    <pattern name="0"><assign>0</assign></pattern>
    <pattern name="1"><assign>1</assign></pattern>
    <pattern name="2"><assign>2</assign></pattern>
    <pattern name="random">
      <import>java.util.Random</import>
      <assign>new Random().nextInt(10)</assign>
    </pattern>
  </arg>
  <arg class="long" >
    <pattern name="minus1L"><assign>-1L</assign></pattern>
    <pattern name="0L"><assign>0L</assign></pattern>
    <pattern name="1L"><assign>1L</assign></pattern>
    <pattern name="2L"><assign>2L</assign></pattern>
  </arg>
  <arg class="double" >
    <pattern name="minus1_0D"><assign>-1.0D</assign></pattern>
    <pattern name="0_0D"><assign>0.0D</assign></pattern>
    <pattern name="0_5D"><assign>0.5D</assign></pattern>
    <pattern name="1_0D"><assign>1.0D</assign></pattern>
  </arg>
  <arg class="boolean" >
    <pattern name="true"><assign>true</assign></pattern>
    <pattern name="false"><assign>false</assign></pattern>
  </arg>
  
  <!-- Primitive wrapper types -->
  <arg class="java.lang.Integer" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="minus1"><assign>-1</assign></pattern>
    <pattern name="0"><assign>0</assign></pattern>
    <pattern name="1"><assign>1</assign></pattern>
    <pattern name="2"><assign>2</assign></pattern>
  </arg>
  <arg class="java.lang.Long" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="minus1L"><assign>-1L</assign></pattern>
    <pattern name="0L"><assign>0L</assign></pattern>
    <pattern name="1L"><assign>1L</assign></pattern>
    <pattern name="2L"><assign>2L</assign></pattern>
  </arg>
  <arg class="java.lang.Double" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="minus1_0D"><assign>-1.0D</assign></pattern>
    <pattern name="0_0D"><assign>0.0D</assign></pattern>
    <pattern name="0_5D"><assign>0.5D</assign></pattern>
    <pattern name="1_0D"><assign>1.0D</assign></pattern>
  </arg>
  <arg class="java.lang.Boolean" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="true"><assign>true</assign></pattern>
    <pattern name="false"><assign>false</assign></pattern>
  </arg>
  <arg class="java.lang.String" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="empty"><assign>""</assign></pattern>
    <pattern name="2"><assign>"2"</assign></pattern>
  </arg>

  <!-- Date time -->
  <arg class="java.util.Date">
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="now"><assign>new Date()</assign></pattern>
  </arg>
  <arg class="java.util.Calendar" >
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="now"><assign>Calendar.getInstance();</assign></pattern>
    <pattern name="Date02_29">
      <assign>Calendar.getInstance();</assign>
      <post-assign>
        {arg}.set(Calendar.YEAR, 2000);
        {arg}.set(Calendar.MONTH, 2 - 1);
        {arg}.set(Calendar.DATE, 29);
        {arg}.set(Calendar.HOUR_OF_DAY, 0);
        {arg}.set(Calendar.MINUTE, 0);
        {arg}.set(Calendar.SECOND, 0);
        {arg}.set(Calendar.MILLISECOND, 0);
      </post-assign>
    </pattern>
    <pattern name="Date12_31">
      <assign>Calendar.getInstance();</assign>
      <post-assign>
        {arg}.set(Calendar.YEAR, 1999);
        {arg}.set(Calendar.MONTH, 12 - 1);
        {arg}.set(Calendar.DATE, 31);
        {arg}.set(Calendar.HOUR_OF_DAY, 0);
        {arg}.set(Calendar.MINUTE, 0);
        {arg}.set(Calendar.SECOND, 0);
        {arg}.set(Calendar.MILLISECOND, 0);
      </post-assign>
    </pattern>
    <pattern name="Date01_01">
      <assign>Calendar.getInstance();</assign>
      <post-assign>
        {arg}.set(Calendar.YEAR, 2000);
        {arg}.set(Calendar.MONTH, 1 - 1);
        {arg}.set(Calendar.DATE, 1);
        {arg}.set(Calendar.HOUR_OF_DAY, 0);
        {arg}.set(Calendar.MINUTE, 0);
        {arg}.set(Calendar.SECOND, 0);
        {arg}.set(Calendar.MILLISECOND, 0);
      </post-assign>
    </pattern>
    <pattern name="Time23_59_59">
      <assign>Calendar.getInstance();</assign>
      <post-assign>{arg}.set(Calendar.YEAR, 1995);
        {arg}.set(Calendar.MONTH, 5 - 1);
        {arg}.set(Calendar.DATE, 24);
        {arg}.set(Calendar.HOUR_OF_DAY, 23);
        {arg}.set(Calendar.MINUTE, 59);
        {arg}.set(Calendar.SECOND, 59);
        {arg}.set(Calendar.MILLISECOND, 0);
      </post-assign>
    </pattern>
    <pattern name="Time00_00_00">
      <assign>Calendar.getInstance();</assign>
      <post-assign>
        {arg}.set(Calendar.YEAR, 1995);
        {arg}.set(Calendar.MONTH, 5 - 1);
        {arg}.set(Calendar.DATE, 25);
        {arg}.set(Calendar.HOUR_OF_DAY, 0);
        {arg}.set(Calendar.MINUTE, 0);
        {arg}.set(Calendar.SECOND, 0);
        {arg}.set(Calendar.MILLISECOND, 0);
      </post-assign>
    </pattern>
  </arg>
  <arg class="org.joda.time.DateTime">
    <import>org.joda.time.format.DateTimeFormat</import>
    <pattern name="null"><assign>null</assign></pattern>
    <pattern name="JavaBirthday">
      <assign>
        DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime("19950525000000")
      </assign>
    </pattern>
  </arg>

  <!-- 
    =============================
     * Assertions *
    =============================

    ****** <XML> ******
      <return class="int">
        <import>static org.hamcrest.Matchers.*</import>
        <import>static org.junit.Assert.*</import>
        <assert>assertThat(actual, is(greaterThanOrEqualTo(0)));</assert>
        <assert>assertThat(actual, is(lessThanOrEqualTo(Integer.MAX_VALUE)));</assert>
      </return>

    ****** <Target> ******
      public int increment() {
        this.i += 1;
        return this.i;
      }

    ****** <Test> ******
      @Test 
      public void increment_A$() throws Exception {
        // TODO auto-generated by JUnit Helper.
        Sample target = new Sample();
        int actual = target.increment();
        assertThat(actual, is(greaterThanOrEqualTo(0)));
        assertThat(actual, is(lessThanOrEqualTo(Integer.MAX_VALUE)));
      }

  -->
  <!-- JUnit 3.x -->
  <!-- 
  <return class="int">
    <assert>assertTrue(actual >= 0);</assert>
  </return>
   -->
  <!-- JUnit 4.x -->
  <return class="int">
    <import>static org.hamcrest.Matchers.*</import>
    <import>static org.junit.Assert.*</import>
    <assert>assertThat(actual, is(greaterThanOrEqualTo(0)));</assert>
    <assert>assertThat(actual, is(lessThanOrEqualTo(Integer.MAX_VALUE)));</assert>
  </return>
  <return class="java.lang.Integer">
    <import>static org.hamcrest.Matchers.*</import>
    <import>static org.junit.Assert.*</import>
    <assert>assertThat(actual, is(greaterThanOrEqualTo(0)));</assert>
    <assert>assertThat(actual, is(lessThanOrEqualTo(Integer.MAX_VALUE)));</assert>
  </return>

</junithelper-extension>
```

