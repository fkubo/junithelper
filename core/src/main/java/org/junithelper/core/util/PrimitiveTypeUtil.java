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
package org.junithelper.core.util;


public class PrimitiveTypeUtil {

    private PrimitiveTypeUtil() {
    }

    public static final boolean isPrimitive(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName == null)
            return false;
        if (typeName.equals("byte"))
            return true;
        if (typeName.equals("short"))
            return true;
        if (typeName.equals("int"))
            return true;
        if (typeName.equals("long"))
            return true;
        if (typeName.equals("char"))
            return true;
        if (typeName.equals("float"))
            return true;
        if (typeName.equals("double"))
            return true;
        if (typeName.equals("boolean"))
            return true;
        if (typeName.equals("void"))
            return true;
        return false;
    }

    public static final Class<?> getPrimitiveClass(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName.equals("byte"))
            return byte.class;
        if (typeName.equals("short"))
            return short.class;
        if (typeName.equals("int"))
            return int.class;
        if (typeName.equals("long"))
            return long.class;
        if (typeName.equals("char"))
            return char.class;
        if (typeName.equals("float"))
            return float.class;
        if (typeName.equals("double"))
            return double.class;
        if (typeName.equals("boolean"))
            return boolean.class;
        if (typeName.equals("void"))
            return void.class;
        throw new IllegalArgumentException("Not primitive type : " + typeName);
    }

    public static final String getTypeDefaultValue(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName.equals("byte"))
            return "0";
        if (typeName.equals("short"))
            return "0";
        if (typeName.equals("int"))
            return "0";
        if (typeName.equals("long"))
            return "0L";
        if (typeName.equals("char"))
            return "'\u0000'";
        if (typeName.equals("float"))
            return "0.0F";
        if (typeName.equals("double"))
            return "0.0";
        if (typeName.equals("boolean"))
            return "false";
        if (typeName.equals("void"))
            return "void";
        throw new IllegalArgumentException("Not primitive type : " + typeName);
    }

    // fk 2012.05.31 setterアクセサ用の値を追加.
    public static final String getTypeDefaultValueForSetter(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName.equals("byte"))
            return "9";
        if (typeName.equals("short"))
            return "9";
        if (typeName.equals("int"))
            return "9";
        if (typeName.equals("long"))
            return "9L";
        if (typeName.equals("char"))
            return "'\u0099'";
        if (typeName.equals("float"))
            return "9.9F";
        if (typeName.equals("double"))
            return "9.9";
        if (typeName.equals("boolean"))
            return "true";
        if (typeName.equals("void"))
            return "void";
        throw new IllegalArgumentException("Not primitive type : " + typeName);
    }

    // fk

    // fk 2012.06.01 PrimitiveWrapperかどうかのメソッドを追加.
    public static final boolean isSimpleObjectType(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName == null)
            return false;
        if (typeName.equals("Byte"))
            return true;
        if (typeName.equals("Short"))
            return true;
        if (typeName.equals("Integer"))
            return true;
        if (typeName.equals("long"))
            return true;
        if (typeName.equals("char"))
            return true;
        if (typeName.equals("float"))
            return true;
        if (typeName.equals("Double"))
            return true;
        if (typeName.equals("Boolean"))
            return true;
        // primitiveではないが追加.
        if (typeName.equals("BigInteger") || typeName.equals("java.math.BigInteger"))
            return true;
        if (typeName.equals("BigDecimal") || typeName.equals("java.math.BigDecimal"))
            return true;
        if (typeName.equals("Date") || typeName.equals("java.util.Date"))
            return true;
        if (typeName.equals("java.sql.Date"))
            return true;
        if (typeName.equals("Timestamp") || typeName.equals("java.sql.Timestamp"))
            return true;
        return false;
    }
    // fk

    // fk 2012.06.01 基本的なオブジェクト型のsetterアクセサ用初期値を追加.
    public static final String getSimpleObjectTypeDefaultValueForSetter(String typeName) {
        Assertion.on("typeName").mustNotBeEmpty(typeName);
        if (typeName.equals("Byte"))
            return "Byte.valueOf((byte)" + getTypeDefaultValueForSetter("byte") + ")";
        if (typeName.equals("Short"))
            return "Short.valueOf((short)" + getTypeDefaultValueForSetter("short") + ")";
        if (typeName.equals("Integer"))
            return "Integer.valueOf(" + getTypeDefaultValueForSetter("int") + ")";
        if (typeName.equals("Long"))
            return "Long.valueOf(" + getTypeDefaultValueForSetter("long") + "L)";
        if (typeName.equals("Character"))
            return "Character.valueOf(" + getTypeDefaultValueForSetter("char") + ")";
        if (typeName.equals("Float"))
            return "Float.valueOf(" + getTypeDefaultValueForSetter("float") + "F)";
        if (typeName.equals("Double"))
            return "Double.valueOf(" + getTypeDefaultValueForSetter("double") + ")";
        if (typeName.equals("Boolean"))
            return "Boolean.valueOf(" + getTypeDefaultValueForSetter("boolean") + ")";
        if (typeName.equals("BigInteger") || typeName.equals("java.math.BigInteger"))
            return "BigInteger.valueOf(999)";
        if (typeName.equals("BigDecimal") || typeName.equals("java.math.BigDecimal"))
            return "BigDecimal.valueOf(999.99)";
        if (typeName.equals("Date") || typeName.equals("java.util.Date"))
            return "Timestamp.valueOf(\"2012-03-04 05:06:07.89\")";
        if (typeName.equals("java.sql.Date"))
            return "java.sql.Date.valueOf(\"2012-03-04 05:06:07.89\")";
        if (typeName.equals("Timestamp") || typeName.equals("java.sql.Timestamp"))
            return "Timestamp.valueOf(\"2012-03-04 05:06:07.89\")";
        throw new IllegalArgumentException("Not primitive type : " + typeName);
    }
    // fk

}
