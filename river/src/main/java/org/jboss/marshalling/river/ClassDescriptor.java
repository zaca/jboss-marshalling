/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.marshalling.river;

import java.io.Externalizable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jboss.marshalling.Pair;
import org.jboss.marshalling.reflect.SerializableClass;
import org.jboss.marshalling.reflect.SerializableClassRegistry;
import org.jboss.marshalling.reflect.SerializableField;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class ClassDescriptor {

    public static final ClassDescriptor STRING_DESCRIPTOR = new SimpleClassDescriptor(String.class, Protocol.ID_STRING_CLASS);
    public static final ClassDescriptor CLASS_DESCRIPTOR = new SimpleClassDescriptor(Class.class, Protocol.ID_CLASS_CLASS);
    public static final ClassDescriptor OBJECT_DESCRIPTOR = new SimpleClassDescriptor(Object.class, Protocol.ID_OBJECT_CLASS);
    public static final ClassDescriptor ENUM_DESCRIPTOR = new SimpleClassDescriptor(Enum.class, Protocol.ID_ENUM_CLASS);
    public static final ClassDescriptor BOOLEAN = new SimpleClassDescriptor(boolean.class, Protocol.ID_PRIM_BOOLEAN);
    public static final ClassDescriptor BYTE = new SimpleClassDescriptor(byte.class, Protocol.ID_PRIM_BYTE);
    public static final ClassDescriptor SHORT = new SimpleClassDescriptor(short.class, Protocol.ID_PRIM_SHORT);
    public static final ClassDescriptor INT = new SimpleClassDescriptor(int.class, Protocol.ID_PRIM_INT);
    public static final ClassDescriptor LONG = new SimpleClassDescriptor(long.class, Protocol.ID_PRIM_LONG);
    public static final ClassDescriptor CHAR = new SimpleClassDescriptor(char.class, Protocol.ID_PRIM_CHAR);
    public static final ClassDescriptor FLOAT = new SimpleClassDescriptor(float.class, Protocol.ID_PRIM_FLOAT);
    public static final ClassDescriptor DOUBLE = new SimpleClassDescriptor(double.class, Protocol.ID_PRIM_DOUBLE);
    public static final ClassDescriptor VOID = new SimpleClassDescriptor(void.class, Protocol.ID_VOID);
    public static final ClassDescriptor BOOLEAN_OBJ = new SimpleClassDescriptor(Boolean.class, Protocol.ID_BOOLEAN_CLASS);
    public static final ClassDescriptor BYTE_OBJ = new SimpleClassDescriptor(Byte.class, Protocol.ID_BYTE_CLASS);
    public static final ClassDescriptor SHORT_OBJ = new SimpleClassDescriptor(Short.class, Protocol.ID_SHORT_CLASS);
    public static final ClassDescriptor INTEGER_OBJ = new SimpleClassDescriptor(Integer.class, Protocol.ID_INTEGER_CLASS);
    public static final ClassDescriptor LONG_OBJ = new SimpleClassDescriptor(Long.class, Protocol.ID_LONG_CLASS);
    public static final ClassDescriptor CHARACTER_OBJ = new SimpleClassDescriptor(Character.class, Protocol.ID_CHARACTER_CLASS);
    public static final ClassDescriptor FLOAT_OBJ = new SimpleClassDescriptor(Float.class, Protocol.ID_FLOAT_CLASS);
    public static final ClassDescriptor DOUBLE_OBJ = new SimpleClassDescriptor(Double.class, Protocol.ID_DOUBLE_CLASS);
    public static final ClassDescriptor VOID_OBJ = new SimpleClassDescriptor(Void.class, Protocol.ID_VOID_CLASS);
    public static final ClassDescriptor BOOLEAN_ARRAY = new SimpleClassDescriptor(boolean[].class, Protocol.ID_BOOLEAN_ARRAY_CLASS);
    public static final ClassDescriptor BYTE_ARRAY = new SimpleClassDescriptor(byte[].class, Protocol.ID_BYTE_ARRAY_CLASS);
    public static final ClassDescriptor SHORT_ARRAY = new SimpleClassDescriptor(short[].class, Protocol.ID_SHORT_ARRAY_CLASS);
    public static final ClassDescriptor INT_ARRAY = new SimpleClassDescriptor(int[].class, Protocol.ID_INT_ARRAY_CLASS);
    public static final ClassDescriptor LONG_ARRAY = new SimpleClassDescriptor(long[].class, Protocol.ID_LONG_ARRAY_CLASS);
    public static final ClassDescriptor CHAR_ARRAY = new SimpleClassDescriptor(char[].class, Protocol.ID_CHAR_ARRAY_CLASS);
    public static final ClassDescriptor FLOAT_ARRAY = new SimpleClassDescriptor(float[].class, Protocol.ID_FLOAT_ARRAY_CLASS);
    public static final ClassDescriptor DOUBLE_ARRAY = new SimpleClassDescriptor(double[].class, Protocol.ID_DOUBLE_ARRAY_CLASS);
    public static final ClassDescriptor ABSTRACT_COLLECTION = new SimpleClassDescriptor(AbstractCollection.class, Protocol.ID_ABSTRACT_COLLECTION);
    public static final ClassDescriptor ABSTRACT_LIST = new SimpleClassDescriptor(AbstractList.class, Protocol.ID_ABSTRACT_LIST);
    public static final ClassDescriptor ABSTRACT_QUEUE = new SimpleClassDescriptor(AbstractQueue.class, Protocol.ID_ABSTRACT_QUEUE);
    public static final ClassDescriptor ABSTRACT_SEQUENTIAL_LIST = new SimpleClassDescriptor(AbstractSequentialList.class, Protocol.ID_ABSTRACT_SEQUENTIAL_LIST);
    public static final ClassDescriptor ABSTRACT_SET = new SimpleClassDescriptor(AbstractSet.class, Protocol.ID_ABSTRACT_SET);
    public static final ClassDescriptor PAIR = new SimpleClassDescriptor(Pair.class, Protocol.ID_PAIR);

    static final ClassDescriptor SINGLETON_MAP = getSerializableClassDescriptor(Protocol.singletonMapClass);
    static final ClassDescriptor SINGLETON_SET = getSerializableClassDescriptor(Protocol.singletonSetClass);
    static final ClassDescriptor SINGLETON_LIST = getSerializableClassDescriptor(Protocol.singletonListClass);
    static final ClassDescriptor EMPTY_MAP = getSerializableClassDescriptor(Protocol.emptyMapClass);
    static final ClassDescriptor EMPTY_SET = getSerializableClassDescriptor(Protocol.emptySetClass);
    static final ClassDescriptor EMPTY_LIST = getSerializableClassDescriptor(Protocol.emptyListClass);
    static final ClassDescriptor CC_ARRAY_LIST = getSerializableClassDescriptor(ArrayList.class);
    static final ClassDescriptor CC_LINKED_LIST = getSerializableClassDescriptor(LinkedList.class);
    static final ClassDescriptor CC_HASH_SET = getSerializableClassDescriptor(HashSet.class);
    static final ClassDescriptor CC_LINKED_HASH_SET = getSerializableClassDescriptor(LinkedHashSet.class);
    static final ClassDescriptor CC_TREE_SET = getSerializableClassDescriptor(TreeSet.class);
    static final ClassDescriptor CC_IDENTITY_HASH_MAP = getSerializableClassDescriptor(IdentityHashMap.class);
    static final ClassDescriptor CC_HASH_MAP = getSerializableClassDescriptor(HashMap.class);
    static final ClassDescriptor CC_HASHTABLE = getSerializableClassDescriptor(Hashtable.class);
    static final ClassDescriptor CC_LINKED_HASH_MAP = getSerializableClassDescriptor(LinkedHashMap.class);
    static final ClassDescriptor CC_TREE_MAP = getSerializableClassDescriptor(TreeMap.class);
    static final ClassDescriptor CC_ENUM_SET = getSerializableClassDescriptor(EnumSet.class);
    static final ClassDescriptor CC_ENUM_MAP = getSerializableClassDescriptor(EnumMap.class);
    static final ClassDescriptor CONCURRENT_HASH_MAP = getSerializableClassDescriptor(ConcurrentHashMap.class);
    static final ClassDescriptor COPY_ON_WRITE_ARRAY_LIST = getSerializableClassDescriptor(CopyOnWriteArrayList.class);
    static final ClassDescriptor COPY_ON_WRITE_ARRAY_SET = getSerializableClassDescriptor(CopyOnWriteArraySet.class);
    static final ClassDescriptor VECTOR = getSerializableClassDescriptor(Vector.class);
    static final ClassDescriptor STACK = getSerializableClassDescriptor(Stack.class);
    static final ClassDescriptor ARRAY_DEQUE = getSerializableClassDescriptor(ArrayDeque.class);
    static final ClassDescriptor REVERSE_ORDER = getSerializableClassDescriptor(Protocol.reverseOrderClass);
    static final ClassDescriptor REVERSE_ORDER2 = getSerializableClassDescriptor(Protocol.reverseOrder2Class);
    static final ClassDescriptor NCOPIES = getSerializableClassDescriptor(Protocol.nCopiesClass);

    private static SerializableClassDescriptor getSerializableClassDescriptor(final Class<?> subject) {
        return AccessController.doPrivileged(new PrivilegedAction<SerializableClassDescriptor>() {
            public SerializableClassDescriptor run() {
                final SerializableClassRegistry reg = SerializableClassRegistry.getInstance();
                final SerializableClass serializableClass = reg.lookup(subject);
                final SerializableField[] fields = serializableClass.getFields();
                final boolean hasWriteObject = serializableClass.hasWriteObject();
                try {
                    return new BasicSerializableClassDescriptor(serializableClass, null, fields, Externalizable.class.isAssignableFrom(subject) ? Protocol.ID_EXTERNALIZABLE_CLASS : hasWriteObject ? Protocol.ID_WRITE_OBJECT_CLASS : Protocol.ID_SERIALIZABLE_CLASS);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        });
    }

    public abstract Class<?> getType();

    public abstract int getTypeID();
}
