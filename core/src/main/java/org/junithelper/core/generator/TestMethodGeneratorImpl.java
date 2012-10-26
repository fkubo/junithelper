/*
 * Copyright 2009-2010 junithelper.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.junithelper.core.generator;

import static org.junithelper.core.generator.GeneratorImplFunction.*;

import java.util.ArrayList;
import java.util.List;

import org.junithelper.core.config.Configuration;
import org.junithelper.core.config.JUnitVersion;
import org.junithelper.core.config.MessageValue;
import org.junithelper.core.config.MockObjectFramework;
import org.junithelper.core.config.TestingPatternExplicitComment;
import org.junithelper.core.config.extension.ExtArgPattern;
import org.junithelper.core.config.extension.ExtInstantiation;
import org.junithelper.core.constant.RegExp;
import org.junithelper.core.constant.StringValue;
import org.junithelper.core.extractor.AvailableTypeDetector;
import org.junithelper.core.meta.ArgTypeMeta;
import org.junithelper.core.meta.ClassMeta;
import org.junithelper.core.meta.ExceptionMeta;
import org.junithelper.core.meta.MethodMeta;
import org.junithelper.core.meta.TestMethodMeta;
import org.junithelper.core.util.Assertion;
import org.junithelper.core.util.PrimitiveTypeUtil;
;

class TestMethodGeneratorImpl implements TestMethodGenerator {

    private final SourceCodeAppender appender;

    private final Configuration config;
    private ClassMeta targetClassMeta;
    private final MessageValue messageValue = new MessageValue();

    public TestMethodGeneratorImpl(Configuration config, LineBreakProvider lineBreakProvider) {
        this.config = config;
        IndentationProvider indentationProvider = new IndentationProvider(config);
        appender = new SourceCodeAppender(lineBreakProvider, indentationProvider);
    }

    @Override
    public void initialize(ClassMeta targetClassMeta) {
        this.targetClassMeta = targetClassMeta;
        messageValue.initialize(config.language);
    }

    @Override
    public TestMethodMeta getTestMethodMeta(MethodMeta targetMethodMeta) {
        return getTestMethodMeta(targetMethodMeta, null);
    }

    @Override
    public TestMethodMeta getTestMethodMeta(MethodMeta targetMethodMeta, ExceptionMeta exception) {
        if (targetClassMeta == null) {
            throw new IllegalStateException("Not initialized");
        }
        TestMethodMeta testMethodMeta = new TestMethodMeta();
        testMethodMeta.classMeta = targetClassMeta;
        testMethodMeta.methodMeta = targetMethodMeta;
        testMethodMeta.testingTargetException = exception;
        return testMethodMeta;
    }

    @Override
    public String getTestMethodNamePrefix(TestMethodMeta testMethodMeta) {
        return getTestMethodNamePrefix(testMethodMeta, null);
    }

    @Override
    public String getTestMethodNamePrefix(TestMethodMeta testMethodMeta, ExceptionMeta exception) {
        MethodMeta targetMethodMeta = testMethodMeta.methodMeta;

        // fk 2012.07.12 prefix追加.
        StringBuilder buf = new StringBuilder();
        // fk

        // testing instantiation
        if (targetMethodMeta == null) {
            if (testMethodMeta.isTypeTest) {
                // fk 2012.07.12 prefix追加.
                buf.append("type");
                // return "type";
                // fk
            } else if (testMethodMeta.isInstantiationTest) {
                // fk 2012.06.20 複数コンストラクタ対応.
                // StringBuilder buf = new StringBuilder();
                buf.append("instantiation");
                if (config.testMethodName.isArgsRequired) {
                    buf.append(config.testMethodName.basicDelimiter);
                    buf.append(config.testMethodName.argsAreaPrefix);
                    if (testMethodMeta.constructorMeta == null) {
                        buf.append(config.testMethodName.argsAreaDelimiter);
                    } else {
                        for (ArgTypeMeta argType : testMethodMeta.constructorMeta.argTypes) {
                            buf.append(config.testMethodName.argsAreaDelimiter);
                            buf.append(argType.nameInMethodName);
                        }
                    }
                }
                // return buf.toString();
                // return "instantiation";
            }
            // fk 2012.07.12 prefix追加.
        } else {
            // }
            // StringBuilder buf = new StringBuilder();
            // fk
            buf.append(targetMethodMeta.name);
            if (config.testMethodName.isArgsRequired) {
                buf.append(config.testMethodName.basicDelimiter);
                buf.append(config.testMethodName.argsAreaPrefix);
                if (targetMethodMeta.argTypes.size() == 0) {
                    buf.append(config.testMethodName.argsAreaDelimiter);
                } else {
                    for (ArgTypeMeta argType : targetMethodMeta.argTypes) {
                        buf.append(config.testMethodName.argsAreaDelimiter);
                        buf.append(argType.nameInMethodName);
                    }
                }
            }
            if (config.testMethodName.isReturnRequired) {
                buf.append(config.testMethodName.basicDelimiter);
                buf.append(config.testMethodName.returnAreaPrefix);
                buf.append(config.testMethodName.returnAreaDelimiter);
                if (targetMethodMeta.returnType.nameInMethodName == null) {
                    buf.append("void");
                } else {
                    buf.append(targetMethodMeta.returnType.nameInMethodName);
                }
            }
            if (exception != null) {
                buf.append(config.testMethodName.basicDelimiter);
                buf.append(config.testMethodName.exceptionAreaPrefix);
                buf.append(config.testMethodName.exceptionAreaDelimiter);
                buf.append(exception.nameInMethodName);
            }
            // extension arg patterns
            if (testMethodMeta.extArgPattern != null) {
                buf.append(config.testMethodName.basicDelimiter);
                buf.append(testMethodMeta.extArgPattern.extArg.getCanonicalClassNameInMethodName());
                buf.append("Is");
                buf.append(testMethodMeta.extArgPattern.getNameWhichFirstCharIsUpper());
            }

            // fk 2012.07.12 prefix追加.
        }
        if (config.testMethodName.prefix != null && !config.testMethodName.prefix.isEmpty() && buf.length() > 0) {
            buf.replace(0, 1, buf.substring(0, 1).toUpperCase());
            buf.insert(0, config.testMethodName.prefix);
        }
        // fk

        return buf.toString();
    }

    @Override
    public String getTestMethodSourceCode(TestMethodMeta testMethodMeta) {

        StringBuilder buf = new StringBuilder();

        // fk 2012.06.01 フィールド定義Mockかパラメータ定義Mockかを判定するフラグ.
        boolean isFieldMock = false;
        boolean hasMock = false;
        // fk

        // JMockit
        // fk 2012.06.01 フラグで出力内容を切り替える.
        if (isFieldMock && config.mockObjectFramework == MockObjectFramework.JMockit) {
            // fk
            // fk 2012.05.29 Mock用のJavaDoc用のコメントを追加.
            List<String[]> mockedFieldsForJMockit = getMockedFieldsForJMockit(testMethodMeta);
            // List<String> mockedFieldsForJMockit =
            // getMockedFieldsForJMockit(testMethodMeta);
            hasMock = !mockedFieldsForJMockit.isEmpty();
            for (String[] mocked : mockedFieldsForJMockit) {
                // for (String mocked : mockedFieldsForJMockit) {
                assert (mocked.length == 4);
                // fk

                // fk 2012.05.29 Mock用JavaDoc作成.
                appender.appendTabs(buf, 1);
                buf.append("/** ");
                buf.append(mocked[1]);
                buf.append("で利用している引数");
                buf.append(mocked[2]);
                buf.append("のMock. */");
                appender.appendLineBreak(buf);
                // fk

                appender.appendTabs(buf, 1);
                buf.append("@Mocked ");
                appender.appendLineBreak(buf);
                appender.appendTabs(buf, 1);
                buf.append(mocked[0]);
                buf.append(StringValue.Semicolon);
                appender.appendLineBreak(buf);
            }
            if (mockedFieldsForJMockit.size() > 0) {
                appender.appendLineBreak(buf);
            }
        }

        // fk 2012.05.29 テストメソッド用JavaDoc作成.コメント変更.
        appender.appendTabs(buf, 1);
        buf.append("/**");
        appender.appendLineBreak(buf);
        appender.appendTabs(buf, 1);
        buf.append(" * ");
        // buf.append(testMethodMeta.classMeta.name+"."+testMethodMeta.methodMeta.name);
        // buf.append(getTestMethodNamePrefix(testMethodMeta,
        // testMethodMeta.testingTargetException));

        if (testMethodMeta.methodMeta != null) {
            buf.append("{@link ");
            buf.append(targetClassMeta.name);
            buf.append("#");
            buf.append(testMethodMeta.methodMeta.name);
            buf.append("(");
            for (int i = 0; i < testMethodMeta.methodMeta.argNames.size(); i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(testMethodMeta.methodMeta.argTypes.get(i).nameInMethodName);
            }
            buf.append(")}用");
        } else if (testMethodMeta.isTypeTest) {
            buf.append("type");
        } else if (testMethodMeta.isInstantiationTest && testMethodMeta.constructorMeta != null) {
            buf.append("{@link ");
            buf.append(targetClassMeta.name);
            buf.append("#");
            buf.append(targetClassMeta.name);
            buf.append("(");
            for (int i = 0; i < testMethodMeta.constructorMeta.argNames.size(); i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(testMethodMeta.constructorMeta.argTypes.get(i).nameInMethodName);
            }
            buf.append(")}用");
        } else if (testMethodMeta.isInstantiationTest) {
            buf.append("instantiation");
        }
        buf.append("テストメソッド.");
        appender.appendLineBreak(buf);

        // モックコメント追加.
        List<String[]> mockedFieldsForJMockit = getMockedFieldsForJMockit(testMethodMeta);
        // fk 2012.10.23 hasMockが設定されない不具合に対応.
        hasMock = !mockedFieldsForJMockit.isEmpty();
        // fk
        for (int i = 0; i < mockedFieldsForJMockit.size(); i++) {
            String[] mocked = mockedFieldsForJMockit.get(i);
            appender.appendTabs(buf, 1);
            buf.append(" * @param ");
            buf.append(mocked[2]);
            buf.append(" 引数のモック");
            appender.appendLineBreak(buf);
        }

        appender.appendTabs(buf, 1);
        buf.append(" */");
        appender.appendLineBreak(buf);
        // fk

        // test method signature
        if (config.junitVersion == JUnitVersion.version3) {
            appender.appendTabs(buf, 1);
            buf.append("public void ");
            buf.append(StringValue.JUnit.TestMethodNamePrefixForJUnitVersion3);
            buf.append(config.testMethodName.basicDelimiter);
        } else {
            appender.appendTabs(buf, 1);
            buf.append("@Test");
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, 1);
            buf.append("public void ");
        }
        buf.append(getTestMethodNamePrefix(testMethodMeta, testMethodMeta.testingTargetException));

        // fk 2012.05.29 テストメソッドの例外を廃止.
        // boolean isThrowableRequired = false;
        // if (testMethodMeta.methodMeta != null &&
        // testMethodMeta.methodMeta.throwsExceptions != null) {
        // for (ExceptionMeta ex : testMethodMeta.methodMeta.throwsExceptions) {
        // if (ex.name.equals("Throwable")) {
        // isThrowableRequired = true;
        // break;
        // }
        // }
        // }
        // buf.append("() throws ");
        // buf.append(isThrowableRequired ? "Throwable" : "Exception");
        // fk

        // fk 2012.06.01 引数でMockを渡すよう変更.
        buf.append("(");
        for (int i = 0; i < mockedFieldsForJMockit.size(); i++) {
            String[] mocked = mockedFieldsForJMockit.get(i);
            if (i > 0) {
                buf.append(", ");
            }
            buf.append("@Mocked final ");
            buf.append(mocked[3]);
            buf.append(" ");
            buf.append(mocked[2]);
        }
        buf.append(")");
        // fk

        buf.append(" {");
        appender.appendLineBreak(buf);

        // fk 2012.06.01 アクセサにTODOメッセージを入れないよう修正.型・生成テストもTODOを除去.
        if ((testMethodMeta.methodMeta == null || !testMethodMeta.methodMeta.isAccessor) && !testMethodMeta.isTypeTest
                && !testMethodMeta.isInstantiationTest) {
            // auto generated todo message
            appender.appendTabs(buf, 2);
            buf.append("// ");
            buf.append(messageValue.getAutoGeneratedTODOMessage());
            appender.appendLineBreak(buf);
        }
        // fk

        if (testMethodMeta.isTypeTest) {
            // --------------------------
            // testing type safe

            appender.appendTabs(buf, 2);
            if (config.junitVersion == JUnitVersion.version3) {
                buf.append("assertNotNull(");
                buf.append(testMethodMeta.classMeta.name);
                buf.append(".class);");
            } else {
                buf.append("assertThat(");
                buf.append(testMethodMeta.classMeta.name);
                buf.append(".class, notNullValue());");
            }
            appender.appendLineBreak(buf);

        } else if (testMethodMeta.isInstantiationTest) {
            // --------------------------
            // testing instantiation

            // fk 2012.06.20 複数コンストラクタ対応.
            String instantiation = getInstantiationSourceCodeTarget(config, appender, testMethodMeta);
            buf.append(instantiation);
            appender.appendTabs(buf, 2);
            if (config.junitVersion == JUnitVersion.version3) {
                buf.append("assertNotNull(target);");
            } else {
                buf.append("assertThat(target, notNullValue());");
            }
            appender.appendLineBreak(buf);

            //            String instantiation = getInstantiationSourceCode(config, appender, testMethodMeta);
            //            buf.append(instantiation);
            //            appender.appendTabs(buf, 2);
            //            if (config.junitVersion == JUnitVersion.version3) {
            //                buf.append("assertNotNull(target);");
            //            } else {
            //                buf.append("assertThat(target, notNullValue());");
            //            }
            //            appender.appendLineBreak(buf);
            // fk

        } else if (config.isTemplateImplementationRequired) {
            // --------------------------
            // testing template

            // Arrange or Given
            // fk 2012.06.01 改行追加.
            appender.appendLineBreak(buf);
            // fk
            // fk 2012.06.01 Arrangeコメント変更.
            if (testMethodMeta.methodMeta != null && testMethodMeta.methodMeta.isAccessor) {
                if (testMethodMeta.methodMeta.name.startsWith("set")) {
                    appendTestingPatternExplicitComment(buf, "Arrange：正常系", 2);
                } else {
                    appendTestingPatternExplicitComment(buf, "Arrange：正常系：初期値", 2);
                }
            } else {
                appendTestingPatternExplicitComment(buf, "Arrange：正常系", 2);
            }
            // fk

            // prepare for Mock object framework
            if (config.mockObjectFramework == MockObjectFramework.JMock2) {
                appender.appendTabs(buf, 2);
                buf.append("Mockery context = new Mockery(){{");
                appender.appendLineBreak(buf);
                appender.appendTabs(buf, 3);
                buf.append("setImposteriser(ClassImposteriser.INSTANCE);");
                appender.appendLineBreak(buf);
                appender.appendTabs(buf, 2);
                buf.append("}};");
                appender.appendLineBreak(buf);
            }
            if (config.mockObjectFramework == MockObjectFramework.EasyMock) {
                appender.appendTabs(buf, 2);
                buf.append("IMocksControl mocks = EasyMock.createControl();");
                appender.appendLineBreak(buf);
            }
            // instantiation if testing an instance method
            if (!testMethodMeta.methodMeta.isStatic) {
                String instantiation = getInstantiationSourceCode(config, appender, testMethodMeta);
                buf.append(instantiation);
            }
            // Mockito BDD
            appendBDDMockitoComment(buf, "given", 2);

            if (testMethodMeta.testingTargetException == null) {
                // --------------------------------
                // Normal pattern testing
                // --------------------------------
                // prepare args
                // fk 2012.06.01 Mockを除外するパラメータを追加.
                if (isFieldMock) {
                    appendPreparingArgs(buf, testMethodMeta, new ArrayList<String[]>());
                } else {
                    appendPreparingArgs(buf, testMethodMeta, mockedFieldsForJMockit);
                }
                // appendPreparingArgs(buf, testMethodMeta);
                // fk
                // mock/stub checking

                // fk 2012.05.28 アクセサも除外するために引数追加.
                appendMockChecking(buf, 2, testMethodMeta, hasMock);
                // appendMockChecking(buf, 2);
                // fk

                // fk 2012.06.01 改行追加.
                appender.appendLineBreak(buf);
                // fk
                // Act or When
                appendTestingPatternExplicitComment(buf, "Act", 2);
                // Mockito BDD
                appendBDDMockitoComment(buf, "when", 2);
                // return value
                if (testMethodMeta.methodMeta.returnType != null && testMethodMeta.methodMeta.returnType.name != null) {
                    appender.appendTabs(buf, 2);
                    buf.append(testMethodMeta.methodMeta.returnType.name);
                    buf.append(" actual = ");
                } else {
                    appender.appendTabs(buf, 2);
                }
                // execute target method
                appendExecutingTargetMethod(buf, testMethodMeta);
                // fk 2012.06.01 改行追加.
                appender.appendLineBreak(buf);
                // fk
                // Assert or Then
                // fk 2012.06.01 Assertコメント変更.
                if (testMethodMeta.methodMeta != null && testMethodMeta.methodMeta.isAccessor) {
                    if (testMethodMeta.methodMeta.name.startsWith("set")) {
                        appendTestingPatternExplicitComment(buf, "Assert：指定した値が返却されること", 2);
                    } else {
                        if (PrimitiveTypeUtil.isPrimitive(testMethodMeta.methodMeta.returnType.name)) {
                            String defaultValue = PrimitiveTypeUtil
                                    .getTypeDefaultValue(testMethodMeta.methodMeta.returnType.name);
                            appendTestingPatternExplicitComment(buf, "Assert：" + defaultValue + "であること", 2);
                        } else {
                            appendTestingPatternExplicitComment(buf, "Assert：nullであること", 2);
                        }

                    }
                } else {
                    appendTestingPatternExplicitComment(buf, "Assert：結果が正しいこと", 2);
                    // fk 2012.10.23 モックが無い時も出力するよう変更.
                    appender.appendTabs(buf, 2);
                    buf.append("// new Verifications(){{");
                    appender.appendLineBreak(buf);
                    appender.appendTabs(buf, 3);
                    buf.append("// ");
                    buf.append(messageValue.getExempliGratia());
                    buf.append(" : ");
                    buf.append("List<String> list = new ArrayList<String>();mock.exec(withCapture(list));assertThat(list, is(not(empty()));assertThat(list.get(0), containsString(\"str\"));");
                    appender.appendLineBreak(buf);
                    appender.appendTabs(buf, 2);
                    buf.append("// }};");
                    appender.appendLineBreak(buf);
                    // fk
                }
                // fk
                // Mockito BDD
                appendBDDMockitoComment(buf, "then", 2);
                appendMockVerifying(buf, 2);
                // check return value
                if (testMethodMeta.methodMeta.returnType != null && testMethodMeta.methodMeta.returnType.name != null) {
                    if (testMethodMeta.extReturn != null) {
                        // The return type matches ext return type
                        if (isCanonicalClassNameUsed(testMethodMeta.extReturn.canonicalClassName,
                                testMethodMeta.methodMeta.returnType.name, targetClassMeta)) {
                            for (String assertion : testMethodMeta.extReturn.asserts) {
                                if (assertion != null && assertion.trim().length() > 0) {
                                    appender.appendExtensionSourceCode(buf, assertion);
                                }
                            }
                        }
                    } else {
                        appender.appendTabs(buf, 2);
                        buf.append(testMethodMeta.methodMeta.returnType.name);
                        buf.append(" expected = ");
                        if (PrimitiveTypeUtil.isPrimitive(testMethodMeta.methodMeta.returnType.name)) {
                            String defaultValue = PrimitiveTypeUtil
                                    .getTypeDefaultValue(testMethodMeta.methodMeta.returnType.name);
                            buf.append(defaultValue);
                        } else {
                            buf.append("null");
                        }
                        buf.append(StringValue.Semicolon);
                        appender.appendLineBreak(buf);
                        appender.appendTabs(buf, 2);
                        if (config.junitVersion == JUnitVersion.version3) {
                            buf.append("assertEquals(expected, actual)");
                        } else {
                            // fk 2012.05.31 デフォルトのassertThatのequalToを除去.
                            buf.append("assertThat(actual, is(expected))");
                            // buf.append("assertThat(actual, is(equalTo(expected)))");
                            // fk
                        }
                        buf.append(StringValue.Semicolon);
                        appender.appendLineBreak(buf);
                    }
                } else {
                    // fk 2012.05.31 setterアクセサの検証式を変更.
                    if (testMethodMeta.methodMeta.isAccessor) {
                        if (testMethodMeta.methodMeta.name.startsWith("set")) {
                            assert (!testMethodMeta.methodMeta.argTypes.isEmpty());
                            appender.appendTabs(buf, 2);
                            if ("boolean".equals(testMethodMeta.methodMeta.argTypes.get(0).name.toLowerCase())) {
                                buf.append("assertThat(target.is");
                            } else {
                                buf.append("assertThat(target.get");
                            }
                            buf.append(testMethodMeta.methodMeta.name.substring(3));
                            buf.append("(), is(");
                            buf.append(testMethodMeta.methodMeta.argNames.get(0));
                            buf.append("))");
                            buf.append(StringValue.Semicolon);
                            appender.appendLineBreak(buf);
                        }
                    }
                    // fk
                }
            } else {
                // --------------------------------
                // Exception pattern testing
                // --------------------------------
                // prepare args
                // fk 2012.06.01 Mockを除外するパラメータを追加.
                if (isFieldMock) {
                    appendPreparingArgs(buf, testMethodMeta, new ArrayList<String[]>());
                } else {
                    appendPreparingArgs(buf, testMethodMeta, mockedFieldsForJMockit);
                }
                // appendPreparingArgs(buf, testMethodMeta);
                // fk
                // mock/stub checking

                // fk 2012.05.29 アクセサも除外するために引数追加.
                appendMockChecking(buf, 2, testMethodMeta, hasMock);
                // appendMockChecking(buf, 2);
                // fk

                // try
                appender.appendTabs(buf, 2);
                buf.append("try {");
                appender.appendLineBreak(buf);
                // Assert or Then
                appendTestingPatternExplicitComment(buf, "Assert", 3);
                // Mockito BDD
                appendBDDMockitoComment(buf, "when", 3);
                // execute target method
                appender.appendTabs(buf, 3);
                appendExecutingTargetMethod(buf, testMethodMeta);
                // fail when no exception
                appender.appendTabs(buf, 3);
                buf.append("fail(\"Expected exception was not thrown!\")");
                buf.append(StringValue.Semicolon);
                appender.appendLineBreak(buf);
                // catch
                appender.appendTabs(buf, 2);
                buf.append("} catch (");
                buf.append(testMethodMeta.testingTargetException.name);
                buf.append(" e) {");
                appender.appendLineBreak(buf);
                // Mockito BDD
                appendBDDMockitoComment(buf, "then", 3);
                appender.appendTabs(buf, 2);
                buf.append("}");
                appender.appendLineBreak(buf);
            }
        }
        appender.appendTabs(buf, 1);
        buf.append("}");
        appender.appendLineBreak(buf);

        return buf.toString();
    }

    void appendPreparingArgs(StringBuilder buf, TestMethodMeta testMethodMeta, List<String[]> mockedFieldsForJMockit) {
        // prepare args
        int argsLen = testMethodMeta.methodMeta.argTypes.size();
        if (argsLen > 0) {
            for (int i = 0; i < argsLen; i++) {

                ArgTypeMeta argTypeMeta = testMethodMeta.methodMeta.argTypes.get(i);
                String typeName = argTypeMeta.name;
                String argName = testMethodMeta.methodMeta.argNames.get(i);

                // fk 2012.06.01 Mockの値は除外するよう修正.
                boolean isMockArg = false;
                for (String[] mocked : mockedFieldsForJMockit){
                    if (argName.equals(mocked[2])) {
                        // 停止.
                        isMockArg = true;
                        break;
                    }
                }
                if (isMockArg){
                    continue;
                }
                // fk

                ExtArgPattern extArgPattern = testMethodMeta.extArgPattern;

                boolean isExtArgPatternTarget = false;
                if (extArgPattern != null
                        && isCanonicalClassNameUsed(extArgPattern.extArg.canonicalClassName, argTypeMeta.name,
                                testMethodMeta.classMeta)) {
                    isExtArgPatternTarget = true;
                }

                ExtInstantiation extInstantiation = null;
                // -----------
                // Extension
                if (config.isExtensionEnabled && config.extConfiguration.extInstantiations != null) {
                    for (ExtInstantiation ins : config.extConfiguration.extInstantiations) {
                        if (isCanonicalClassNameUsed(ins.canonicalClassName, argTypeMeta.name, testMethodMeta.classMeta)) {
                            extInstantiation = ins;
                            // add import list
                            for (String newImport : ins.importList) {
                                testMethodMeta.classMeta.importedList.add(newImport);
                            }
                            break;
                        }
                    }
                }

                // --------------------------
                // extension : pre-assign
                if (isExtArgPatternTarget) {
                    // arg patterns
                    if (extArgPattern.preAssignCode != null) {
                        appender.appendExtensionSourceCode(buf, extArgPattern.preAssignCode);
                    }
                } else {
                    if (extInstantiation != null && extInstantiation.preAssignCode != null
                            && extInstantiation.preAssignCode.trim().length() > 0) {
                        appender.appendExtensionSourceCode(buf, extInstantiation.preAssignCode);
                    }
                }

                appender.appendTabs(buf, 2);
                // fk 2012.05.28 JMockitでも引数をfinalになるよう変更.
                if (config.mockObjectFramework == MockObjectFramework.JMock2
                        || config.mockObjectFramework == MockObjectFramework.JMockit) {
                    // if (config.mockObjectFramework ==
                    // MockObjectFramework.JMock2) {
                    buf.append("final ");
                }
                // fk
                buf.append(typeName);
                buf.append(" ");
                buf.append(argName);
                buf.append(" = ");
                if (isExtArgPatternTarget) {
                    buf.append(extArgPattern.assignCode.trim());
                    // --------------------------
                    // extension : assign
                    if (!extArgPattern.assignCode.endsWith(StringValue.Semicolon)) {
                        buf.append(StringValue.Semicolon);
                    }
                } else {
                    // simply instantiation or extension instantiation
                    buf.append(getArgValue(testMethodMeta, argTypeMeta, argName));
                    buf.append(StringValue.Semicolon);
                }
                appender.appendLineBreak(buf);

                // --------------------------
                // extension : post-assign
                // arg patterns
                if (isExtArgPatternTarget) {
                    if (extArgPattern.postAssignCode != null) {
                        appender.appendExtensionPostAssignSourceCode(buf, extArgPattern.postAssignCode, new String[] {
                                "\\{arg\\}", "\\{instance\\}" }, argName);
                    }
                } else {
                    if (extInstantiation != null && extInstantiation.postAssignCode != null
                            && extInstantiation.postAssignCode.trim().length() > 0) {
                        appender.appendExtensionPostAssignSourceCode(buf, extInstantiation.postAssignCode,
                                new String[] { "\\{arg\\}", "\\{instance\\}" }, argName);
                    }
                }
            }
        }
    }

    // fk 2012.05.28 アクセサも除外するために引数追加.
    void appendMockChecking(StringBuilder buf, int depth, TestMethodMeta testMethodMeta, boolean hasMock) {
        // void appendMockChecking(StringBuilder buf, int depth) {
        // fk

        // fk 2012.05.28 isAccessor の時も出力しない.
        if (testMethodMeta.methodMeta != null && testMethodMeta.methodMeta.isAccessor) {
            return;
        }
        // fk

        if (config.mockObjectFramework == MockObjectFramework.EasyMock) {
            appender.appendTabs(buf, depth);
            buf.append("// ");
            buf.append(messageValue.getExempliGratia());
            buf.append(" : ");
            buf.append("EasyMock.expect(mocked.called()).andReturn(1);");
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, depth);
            buf.append("mocks.replay();");
            appender.appendLineBreak(buf);
        } else if (config.mockObjectFramework == MockObjectFramework.JMock2) {
            appender.appendTabs(buf, depth);
            buf.append("context.checking(new Expectations(){{");
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, depth + 1);
            buf.append("// ");
            buf.append(messageValue.getExempliGratia());
            buf.append(" : ");
            buf.append("allowing(mocked).called(); will(returnValue(1));");
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, depth);
            buf.append("}});");
            appender.appendLineBreak(buf);
        } else if (config.mockObjectFramework == MockObjectFramework.JMockit) {
            // fk 2012.10.23 モックが無い時も出力するよう変更.
            appender.appendTabs(buf, depth);
            buf.append("// new Expectations(){{");
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, depth + 1);
            buf.append("// ");
            buf.append(messageValue.getExempliGratia());
            buf.append(" : ");
            // fk

            // fk 2012.06.08 コメント修正.
            buf.append("mocked.get(anyString); result = 200;");
            // buf.append("mocked.get(anyString); returns(200);");
            // fk

            // fk 2012.10.23 モックが無い時も出力するよう変更.
            appender.appendLineBreak(buf);
            appender.appendTabs(buf, depth);
            buf.append("// }};");
            appender.appendLineBreak(buf);
            // fk
        } else if (config.mockObjectFramework == MockObjectFramework.Mockito) {
            appender.appendTabs(buf, depth);
            buf.append("// ");
            buf.append(messageValue.getExempliGratia());
            buf.append(" : ");
            buf.append("given(mocked.called()).willReturn(1);");
            appender.appendLineBreak(buf);
        }
    }

    void appendMockVerifying(StringBuilder buf, int depth) {
        // verfiy
        if (config.mockObjectFramework == MockObjectFramework.EasyMock) {
            appender.appendTabs(buf, depth);
            buf.append("mocks.verify();");
            appender.appendLineBreak(buf);
        }
        if (config.mockObjectFramework == MockObjectFramework.Mockito) {
            appender.appendTabs(buf, depth);
            buf.append("// ");
            buf.append(messageValue.getExempliGratia());
            buf.append(" : ");
            buf.append("verify(mocked).called();");
            appender.appendLineBreak(buf);
        }
    }

    void appendExecutingTargetMethod(StringBuilder buf, TestMethodMeta testMethodMeta) {
        String actor = (testMethodMeta.methodMeta.isStatic) ? testMethodMeta.classMeta.name : "target";
        buf.append(actor);
        buf.append(".");
        buf.append(testMethodMeta.methodMeta.name);
        buf.append("(");
        int argsLen = testMethodMeta.methodMeta.argTypes.size();
        if (argsLen > 0) {
            buf.append(testMethodMeta.methodMeta.argNames.get(0));
        }
        if (argsLen > 1) {
            for (int i = 1; i < argsLen; i++) {
                buf.append(StringValue.Comma);
                buf.append(StringValue.Space);
                buf.append(testMethodMeta.methodMeta.argNames.get(i));
            }
        }
        buf.append(")");
        buf.append(StringValue.Semicolon);
        appender.appendLineBreak(buf);
    }

    void appendBDDMockitoComment(StringBuilder buf, String value, int depth) {
        if (config.mockObjectFramework == MockObjectFramework.Mockito) {
            appender.appendTabs(buf, depth);
            buf.append("// ");
            buf.append(value);
            appender.appendLineBreak(buf);
        }
    }

    void appendTestingPatternExplicitComment(StringBuilder buf, String value, int depth) {
        if (config.testingPatternExplicitComment != TestingPatternExplicitComment.None
                && config.mockObjectFramework != MockObjectFramework.Mockito) {
            if (config.testingPatternExplicitComment == TestingPatternExplicitComment.GivenWhenThen) {
                if (value.equals("Arrange")) {
                    value = "Given";
                } else if (value.equals("Act")) {
                    value = "When";
                } else if (value.equals("Assert")) {
                    value = "Then";
                }
            }
            appender.appendTabs(buf, depth);
            buf.append("// ");
            buf.append(value);
            appender.appendLineBreak(buf);
        }
    }

    // fk 2012.05.31 Mock用のJavaDoc用のコメントを追加.
    List<String[]> getMockedFieldsForJMockit(TestMethodMeta testMethodMeta) {
        List<String[]> dest = new ArrayList<String[]>();
        // fk
        if (testMethodMeta.methodMeta != null) {
            int len = testMethodMeta.methodMeta.argTypes.size();
            for (int i = 0; i < len; i++) {
                String typeName = testMethodMeta.methodMeta.argTypes.get(i).name;
                if (PrimitiveTypeUtil.isPrimitive(typeName)) {
                    continue;
                }
                // fk 2012/06/01 モック作成条件追加.
                if (PrimitiveTypeUtil.isSimpleObjectType(typeName)) {
                    continue;
                }
                // fk
                if (!new AvailableTypeDetector(targetClassMeta).isJMockitMockableType(typeName)) {
                    continue;
                }
                ArgTypeMeta argTypeMeta = testMethodMeta.methodMeta.argTypes.get(i);
                String argName = testMethodMeta.methodMeta.argNames.get(i);
                String value = getArgValue(testMethodMeta, argTypeMeta, argName);
                // fk 2012.05.29 Mockの変数名をカスタマイズ.
                if (value.equals(getJMockitName(testMethodMeta, testMethodMeta.testingTargetException, argName))) {
                    // if (value.equals("this."
                    // + getTestMethodNamePrefix(testMethodMeta,
                    // testMethodMeta.testingTargetException) + "_"
                    // + argName)) {
                    // fk

                    // fk 2012.05.31 Mock用のJavaDoc用のコメントを追加.個別の型も追加.
                    dest.add(new String[] { argTypeMeta.name + " " + value.replace("this.", ""),
                            getTestMethodNamePrefix(testMethodMeta, testMethodMeta.testingTargetException), argName,
                            typeName });
                    // dest.add(argTypeMeta.name + " " + value.replace("this.",
                    // ""));
                    // fk
                }
            }
        }
        return dest;
    }

    // fk 2012.05.31 コメント追加のみ.
    // テスト前に設定する変数値.
    // fk
    String getArgValue(TestMethodMeta testMethodMeta, ArgTypeMeta argTypeMeta, String argName) {

        Assertion.on("testMethodMeta").mustNotBeNull(testMethodMeta);
        Assertion.on("argTypeMeta").mustNotBeNull(argTypeMeta);
        Assertion.on("argName").mustNotBeEmpty(argName);

        // -----------
        // Extension
        if (config.isExtensionEnabled && config.extConfiguration.extInstantiations != null) {
            for (ExtInstantiation ins : config.extConfiguration.extInstantiations) {
                if (isCanonicalClassNameUsed(ins.canonicalClassName, argTypeMeta.name, testMethodMeta.classMeta)) {
                    return ins.assignCode.trim();
                }
            }
        }
        AvailableTypeDetector availableTypeDetector = new AvailableTypeDetector(targetClassMeta);

        // fk 2012.06.01 判定順番の変更 isPrimitiveXxxを先に.
        // fk
        if (PrimitiveTypeUtil.isPrimitive(argTypeMeta.name)) {
            // fk 2012.05.31 setterアクセサのStringだけ特別扱い.
            if (testMethodMeta.methodMeta.name.startsWith("set") && testMethodMeta.methodMeta.isAccessor) {
                return PrimitiveTypeUtil.getTypeDefaultValueForSetter(argTypeMeta.name);
            }
            // fk
            return PrimitiveTypeUtil.getTypeDefaultValue(argTypeMeta.name);
            // fk 2012.06.01 primitiveWrapperの処理を追加.
        } else if (PrimitiveTypeUtil.isSimpleObjectType(argTypeMeta.name)) {
            if (testMethodMeta.methodMeta.name.startsWith("set") && testMethodMeta.methodMeta.isAccessor) {
                return PrimitiveTypeUtil.getSimpleObjectTypeDefaultValueForSetter(argTypeMeta.name);
            }
            return "null";
            // fk
        } else if (availableTypeDetector.isJavaLangPackageType(argTypeMeta.name) && testMethodMeta.methodMeta != null) {
            // fk 2012.06.01
            // setterアクセサのString,Object,BigInteger,BigDecimalだけ特別扱い.
            if (testMethodMeta.methodMeta.name.startsWith("set") && testMethodMeta.methodMeta.isAccessor) {
                if ("String".equals(argTypeMeta.name) || "Object".equals(argTypeMeta.name)) {
                    return "\"" + argName + "\"";
                } else if ("BigInteger".equals(argTypeMeta.name)) {
                    return "BigInteger.valueOf(999)";
                } else if ("BigDecimal".equals(argTypeMeta.name)) {
                    return "BigDecimal.valueOf(999.99)";
                } else {
                    return "null";
                }
            }
            // fk
            return "null";
        } else if (argTypeMeta.name.matches(".+?\\[\\]$")) {
            return "new " + argTypeMeta.name + " {}";
        } else if (argTypeMeta.name.matches("List(<[^>]+>)?")
                && availableTypeDetector.isAvailableType("java.util.List", config)) {
            targetClassMeta.importedList.add("java.util.ArrayList");
            String genericsString = argTypeMeta.getGenericsAsString();
            if (genericsString.equals("<?>")) {
                genericsString = "";
            }
            return "new ArrayList" + genericsString + "()";
        } else if (argTypeMeta.name.matches("Map(<[^>]+>)?")
                && availableTypeDetector.isAvailableType("java.util.Map", config)) {
            targetClassMeta.importedList.add("java.util.HashMap");
            String genericsString = argTypeMeta.getGenericsAsString();
            if (genericsString.matches("<.*\\?.*>")) {
                genericsString = "";
            }
            return "new HashMap" + genericsString + "()";
        } else if (config.mockObjectFramework == MockObjectFramework.EasyMock) {
            return "mocks.createMock(" + argTypeMeta.name.replaceAll(RegExp.Generics, StringValue.Empty) + ".class)";
        } else if (config.mockObjectFramework == MockObjectFramework.JMock2) {
            return "context.mock(" + argTypeMeta.name.replaceAll(RegExp.Generics, StringValue.Empty) + ".class)";
        } else if (config.mockObjectFramework == MockObjectFramework.JMockit) {
            if (new AvailableTypeDetector(targetClassMeta).isJMockitMockableType(argTypeMeta.name)) {
                // fk 2012.05.29 Mockの変数名をカスタマイズ.
                return getJMockitName(testMethodMeta, testMethodMeta.testingTargetException, argName);
                // return "this." + getTestMethodNamePrefix(testMethodMeta,
                // testMethodMeta.testingTargetException) + "_"
                // + argName;
                // fk
            } else {
                return "null";
            }
        } else if (config.mockObjectFramework == MockObjectFramework.Mockito) {
            return "mock(" + argTypeMeta.name.replaceAll(RegExp.Generics, StringValue.Empty) + ".class)";
        } else {
            return "null";
        }
    }

    // fk 2012.05.29 Mockの変数名をカスタマイズ.
    private String getJMockitName(TestMethodMeta testMethodMeta, ExceptionMeta exceptionMeta, String argName) {

        assert (argName != null && argName.length() > 0);

        // 元の実装を変更し、_区切りではなくて、大文字でつなぐ.
        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(getTestMethodNamePrefix(testMethodMeta, testMethodMeta.testingTargetException));
        sb.append(argName.substring(0, 1).toUpperCase());
        if (argName.length() > 1) {
            sb.append(argName.substring(1));
        }
        return sb.toString();
        // return "this." + getTestMethodNamePrefix(testMethodMeta,
        // exceptionMeta)
        // + "_" + argName;
    }
    // fk
}
