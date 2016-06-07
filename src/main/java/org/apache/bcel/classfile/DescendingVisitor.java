/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.bcel.classfile;

import java.util.Stack;

/**
 * Traverses a JavaClass with another Visitor object 'piggy-backed' that is
 * applied to all components of a JavaClass object. I.e. this class supplies the
 * traversal strategy, other classes can make use of it.
 * 
 * @version $Id$
 */
public class DescendingVisitor implements Visitor
{
    private final JavaClass clazz;

    private final Visitor visitor;

    private final Stack<Object> stack = new Stack<>();

    /**
     * @return container of current entitity, i.e., predecessor during traversal
     */
    public Object predecessor()
    {
        return predecessor(0);
    }

    /**
     * @param level
     *            nesting level, i.e., 0 returns the direct predecessor
     * @return container of current entitity, i.e., predecessor during traversal
     */
    public Object predecessor(final int level)
    {
        int size = stack.size();
        if ((size < 2) || (level < 0))
        {
            return null;
        }
        return stack.elementAt(size - (level + 2)); // size - 1 == current
    }

    /**
     * @return current object
     */
    public Object current()
    {
        return stack.peek();
    }

    /**
     * @param clazz
     *            Class to traverse
     * @param visitor
     *            visitor object to apply to all components
     */
    public DescendingVisitor(final JavaClass clazz, final Visitor visitor)
    {
        this.clazz = clazz;
        this.visitor = visitor;
    }

    /**
     * Start traversal.
     */
    public void visit()
    {
        clazz.accept(this);
    }

    @Override
    public void visitJavaClass(final JavaClass _clazz)
    {
        stack.push(_clazz);
        _clazz.accept(visitor);
        Field[] fields = _clazz.getFields();
        for (Field field : fields) {
            field.accept(this);
        }
        Method[] methods = _clazz.getMethods();
        for (Method method : methods) {
            method.accept(this);
        }
        Attribute[] attributes = _clazz.getAttributes();
        for (Attribute attribute : attributes) {
            attribute.accept(this);
        }
        _clazz.getConstantPool().accept(this);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitAnnotation(final Annotations annotation)
    {
        stack.push(annotation);
        annotation.accept(visitor);
        AnnotationEntry[] entries = annotation.getAnnotationEntries();
        for (AnnotationEntry entrie : entries) {
            entrie.accept(this);
        }
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitAnnotationEntry(final AnnotationEntry annotationEntry)
    {
        stack.push(annotationEntry);
        annotationEntry.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitField(final Field field)
    {
        stack.push(field);
        field.accept(visitor);
        Attribute[] attributes = field.getAttributes();
        for (Attribute attribute : attributes) {
            attribute.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitConstantValue(final ConstantValue cv)
    {
        stack.push(cv);
        cv.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitMethod(final Method method)
    {
        stack.push(method);
        method.accept(visitor);
        Attribute[] attributes = method.getAttributes();
        for (Attribute attribute : attributes) {
            attribute.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitExceptionTable(final ExceptionTable table)
    {
        stack.push(table);
        table.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitCode(final Code code)
    {
        stack.push(code);
        code.accept(visitor);
        CodeException[] table = code.getExceptionTable();
        for (CodeException element : table) {
            element.accept(this);
        }
        Attribute[] attributes = code.getAttributes();
        for (Attribute attribute : attributes) {
            attribute.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitCodeException(final CodeException ce)
    {
        stack.push(ce);
        ce.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitLineNumberTable(final LineNumberTable table)
    {
        stack.push(table);
        table.accept(visitor);
        LineNumber[] numbers = table.getLineNumberTable();
        for (LineNumber number : numbers) {
            number.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitLineNumber(final LineNumber number)
    {
        stack.push(number);
        number.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitLocalVariableTable(final LocalVariableTable table)
    {
        stack.push(table);
        table.accept(visitor);
        LocalVariable[] vars = table.getLocalVariableTable();
        for (LocalVariable var : vars) {
            var.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitStackMap(final StackMap table)
    {
        stack.push(table);
        table.accept(visitor);
        StackMapEntry[] vars = table.getStackMap();
        for (StackMapEntry var : vars) {
            var.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitStackMapEntry(final StackMapEntry var)
    {
        stack.push(var);
        var.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
    @Override
    public void visitStackMapTable(StackMapTable table)
    {
        stack.push(table);
        table.accept(visitor);
        StackMapTableEntry[] vars = table.getStackMapTable();
        for (StackMapTableEntry var : vars) {
            var.accept(this);
        }
        stack.pop();
    }
     */

    /**
     * @since 6.0
    @Override
    public void visitStackMapTableEntry(StackMapTableEntry var)
    {
        stack.push(var);
        var.accept(visitor);
        stack.pop();
    }
     */

    @Override
    public void visitLocalVariable(final LocalVariable var)
    {
        stack.push(var);
        var.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantPool(final ConstantPool cp)
    {
        stack.push(cp);
        cp.accept(visitor);
        Constant[] constants = cp.getConstantPool();
        for (int i = 1; i < constants.length; i++)
        {
            if (constants[i] != null)
            {
                constants[i].accept(this);
            }
        }
        stack.pop();
    }

    @Override
    public void visitConstantClass(final ConstantClass constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantDouble(final ConstantDouble constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantFieldref(final ConstantFieldref constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantFloat(final ConstantFloat constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantInteger(final ConstantInteger constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantInterfaceMethodref(
            final ConstantInterfaceMethodref constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitConstantInvokeDynamic(
            final ConstantInvokeDynamic constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantLong(final ConstantLong constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantMethodref(final ConstantMethodref constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantNameAndType(final ConstantNameAndType constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantString(final ConstantString constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitConstantUtf8(final ConstantUtf8 constant)
    {
        stack.push(constant);
        constant.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitInnerClasses(final InnerClasses ic)
    {
        stack.push(ic);
        ic.accept(visitor);
        InnerClass[] ics = ic.getInnerClasses();
        for (InnerClass ic2 : ics) {
            ic2.accept(this);
        }
        stack.pop();
    }

    @Override
    public void visitInnerClass(final InnerClass inner)
    {
        stack.push(inner);
        inner.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitBootstrapMethods(final BootstrapMethods bm)
    {
        stack.push(bm);
        bm.accept(visitor);
        // BootstrapMethod[] bms = bm.getBootstrapMethods();
        // for (int i = 0; i < bms.length; i++)
        // {
        //     bms[i].accept(this);
        // }
        stack.pop();
    }

    @Override
    public void visitDeprecated(final Deprecated attribute)
    {
        stack.push(attribute);
        attribute.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitSignature(final Signature attribute)
    {
        stack.push(attribute);
        attribute.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitSourceFile(final SourceFile attribute)
    {
        stack.push(attribute);
        attribute.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitSynthetic(final Synthetic attribute)
    {
        stack.push(attribute);
        attribute.accept(visitor);
        stack.pop();
    }

    @Override
    public void visitUnknown(final Unknown attribute)
    {
        stack.push(attribute);
        attribute.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitAnnotationDefault(final AnnotationDefault obj)
    {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitEnclosingMethod(final EnclosingMethod obj)
    {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitLocalVariableTypeTable(final LocalVariableTypeTable obj)
    {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitParameterAnnotation(final ParameterAnnotations obj)
    {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /**
     * @since 6.0
     */
    @Override
    public void visitMethodParameters(final MethodParameters obj)
    {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /** @since 6.0 */
    @Override
    public void visitConstantMethodType(final ConstantMethodType obj) {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /** @since 6.0 */
    @Override
    public void visitConstantMethodHandle(final ConstantMethodHandle obj) {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

    /** @since 6.0 */
    @Override
    public void visitParameterAnnotationEntry(final ParameterAnnotationEntry obj) {
        stack.push(obj);
        obj.accept(visitor);
        stack.pop();
    }

}
