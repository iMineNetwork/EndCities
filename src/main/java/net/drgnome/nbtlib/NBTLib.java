// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/
package net.drgnome.nbtlib;

import java.lang.reflect.*;
import java.util.*;
import org.bukkit.Bukkit;

/**
 * <p>
 * The "main" class and base of NBTLib.</p>
 * <p>
 * If you're just using NBT Tags, see the {@link NBT NBT Class}.</p>
 * <p>
 * If you need to access other methods or fields of Minecraft or Craftbukkit, this is for you.</p>
 * <p>
 * <i><b>Note:</b> When accessing private or protected fields of methods, you <b><u>must</u></b> specify the class declaring the field or method! Subclasses will not work!!</i></p>
 */
@SuppressWarnings("unchecked")
public class NBTLib {

    private static final String _minecraft;
    private static final String _craftbukkit;
    private static final String _pversion;
    private static boolean _disabled;

    static {
        final String mc = "net.minecraft.server";
        final String cb = "org.bukkit.craftbukkit";
        try {
            invoke("sun.reflect.Reflection", null, "registerFieldsToFilter", new Class[]{Class.class, String[].class}, new Object[]{NBTLib.class, new String[]{"_disabled"}});
        } catch (Throwable t) {
        }
        boolean disabled = true;
        String craftbukkit = Bukkit.getServer().getClass().getPackage().getName();
        if (!craftbukkit.startsWith(cb)) {
            ArrayList<String> list = new ArrayList<String>();
            for (Package p : Package.getPackages()) {
                String name = p.getName();
                if (name.startsWith(cb + ".v")) {
                    list.add(name);
                }
            }
            if (list.size() == 1) {
                craftbukkit = list.get(0);
            } else {
                NPlugin._log.severe("[NBTLib] Can't find Craftbukkit package! " + list.size() + " possible packages found:");
                for (String s : list) {
                    NPlugin._log.severe("[NBTLib] " + s);
                }
            }
        }
        String pversion = craftbukkit.substring(23);
        String minecraft = mc + "." + pversion;
        try {
            findClass(minecraft + ".World");
            disabled = false;
        } catch (ClassNotFoundException cnfe1) {
            try {
                findClass(minecraft + ".Entity");
                disabled = false;
            } catch (ClassNotFoundException cnfe2) {
                try {
                    findClass(minecraft + ".Block");
                    disabled = false;
                } catch (ClassNotFoundException cnfe3) {
                    try {
                        /*Class clazz = net.minecraft.server.v1_6_R3.EntityHuman.class;
                         clazz = findClass("net.minecraft." + "server".toLowerCase() + ".v1_6_R3.EntityHuman");*/
                        findClass(minecraft + ".Item");
                        disabled = false;
                    } catch (ClassNotFoundException cnfe4) {
                        NPlugin._log.severe("[NBTLib] Can't find Minecraft package! (" + minecraft + "/" + craftbukkit + ")");
                        //cnfe4.printStackTrace();
                    }
                }
            }
        }
        _disabled = disabled;
        _pversion = pversion;
        _minecraft = minecraft + ".";
        _craftbukkit = craftbukkit + ".";
        if (!_disabled) {
            if (NBT.internal0() == 0) // Used to trigger class initialization
            {
                NPlugin._log.severe("[NBTLib] Can't find NBT constructors!");
                _disabled = true;
            }
        }
    }

    private static Class findClass(String name)
            throws ClassNotFoundException {
        return Class.forName(name);
    }

    /**
     * Returns {@code true} if NBTLib is enabled and {@code false} otherwise.
     *
     * @return {@code true} if NBTLib is enabled, {@code false} otherwise.
     */
    public static boolean enabled() {
        return !_disabled;
    }

    /**
     * Returns a {@link Class} from the Minecraft package.
     *
     * @param className The name of the class.
     *
     * @return The class from the Minecraft package.
     *
     * @throws ClassNotFoundException If the class could not be found.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     *
     * @since 0.3
     */
    public static Class getMinecraftClass(String className) throws ClassNotFoundException, NBTLibDisabledException {
        return findClass(getMinecraftPackage() + className);
    }

    /**
     * Returns a {@link Class} from the Craftbukkit package.
     *
     * @param className The name of the class.
     *
     * @return The class from the Craftbukkit package.
     *
     * @throws ClassNotFoundException If the class could not be found.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     *
     * @since 0.3
     */
    public static Class getCraftbukkitClass(String className) throws ClassNotFoundException, NBTLibDisabledException {
        return findClass(getCraftbukkitPackage() + className);
    }

    /**
     * <p>
     * Returns the Minecraft package version.</p>
     * <p>
     * Example: {@code v1_7_R1}</p>
     *
     * @return The Minecraft package version.
     */
    public static String getPackageVersion() {
        return _pversion;
    }

    /**
     * <p>
     * Returns the Minecraft version.</p>
     * <p>
     * This simply calls {@code Bukkit.getVersion()}.</p>
     *
     * @return The Minecraft version.
     */
    public static String getMinecraftVersion() {
        return Bukkit.getVersion();
    }

    /**
     * Returns the Minecraft package name <b>with</b> a trailing dot. Example: {@code net.minecraft.server.v1_7_R1.}
     *
     * @return The Minecraft package name.
     *
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static String getMinecraftPackage()
            throws NBTLibDisabledException {
        if (_disabled) {
            throw new NBTLibDisabledException();
        }
        return _minecraft;
    }

    /**
     * Returns the Craftbukkit package name <b>with</b> a trailing dot. Example: {@code org.bukkit.craftbukkit.v1_7_R1.}
     *
     * @return The Craftbukkit package name.
     *
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static String getCraftbukkitPackage()
            throws NBTLibDisabledException {
        if (_disabled) {
            throw new NBTLibDisabledException();
        }
        return _craftbukkit;
    }

    /**
     * <p>
     * Fetches the value of a field of a Minecraft class without knowing the name of the field.</p>
     *
     * @param className The name of the class.
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static Object fetchDynamicMinecraftField(String className, Object object, Object type)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        return fetchDynamicField(getMinecraftPackage() + className, object, type);
    }

    /**
     * <p>
     * Fetches the value of a field of a Minecraft class.</p>
     *
     * @param className The name of the class.
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param name The name of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static Object fetchMinecraftField(String className, Object object, String name)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        return fetchField(getMinecraftPackage() + className, object, name);
    }

    /**
     * <p>
     * Fetches the value of a field of a Craftbukkit class without knowing the name of the field.</p>
     *
     * @param className The name of the class (<b>with</b> leading subpackages!).
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static Object fetchDynamicCraftbukkitField(String className, Object object, Object type)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        return fetchDynamicField(getCraftbukkitPackage() + className, object, type);
    }

    /**
     * <p>
     * Fetches the value of a field of a Craftbukkit class.</p>
     *
     * @param className The name of the class (<b>with</b> leading subpackages!).
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param name The name of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static Object fetchCraftbukkitField(String className, Object object, String name)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        return fetchField(getCraftbukkitPackage() + className, object, name);
    }

    /**
     * <p>
     * Tries to identify a field of class by the fields type and returns the value of the first found field.</p>
     *
     * @param className The full name of the class.
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static Object fetchDynamicField(String className, Object object, Object type)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        return getField(findClass(className), parseClass(type)).get(object);
    }

    /**
     * <p>
     * Fetches the value of a field of a class.</p>
     *
     * @param className The full name of the class.
     * @param object The instance of which the fields value should be fetched. Use {@code null} for static fields.
     * @param name The name of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static Object fetchField(String className, Object object, String name)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        return getField(findClass(className), name).get(object);
    }

    /**
     * <p>
     * Puts a value into a field of a Minecraft class without knowing the name of the field.</p>
     *
     * @param className The name of the class.
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static void putDynamicMinecraftField(String className, Object object, Object type, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        putDynamicField(getMinecraftPackage() + className, object, type, value);
    }

    /**
     * <p>
     * Puts a value into a field of a Minecraft class.</p>
     *
     * @param className The name of the class.
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param name The name of the field.
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static void putMinecraftField(String className, Object object, String name, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        putField(getMinecraftPackage() + className, object, name, value);
    }

    /**
     * <p>
     * Puts a value into a field of a Craftbukkit class without knowing the name of the field.</p>
     *
     * @param className The name of the class (<b>with</b> leading subpackages!).
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static void putDynamicCraftbukkitField(String className, Object object, Object type, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        putDynamicField(getCraftbukkitPackage() + className, object, type, value);
    }

    /**
     * <p>
     * Puts a value into a field of a Craftbukkit class.</p>
     *
     * @param className The name of the class (<b>with</b> leading subpackages!).
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param name The name of the field.
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     * @throws NBTLibDisabledException If {@link NBTLib} has been disabled.
     */
    public static void putCraftbukkitField(String className, Object object, String name, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NBTLibDisabledException {
        putField(getCraftbukkitPackage() + className, object, name, value);
    }

    /**
     * <p>
     * Tries to identify a field of class by the fields type and puts the value into the first found field.</p>
     *
     * @param className The full name of the class.
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param type The type of the field, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static void putDynamicField(String className, Object object, Object type, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        getField(findClass(className), parseClass(type)).set(object, value);
    }

    /**
     * <p>
     * Puts a value into a field of a class.</p>
     *
     * @param className The full name of the class.
     * @param object The instance of which the fields value should be changed. Use {@code null} for static fields.
     * @param name The name of the field.
     * @param value The new value of the field.
     *
     * @throws ClassNotFoundException If the class {@code className} cannot be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static void putField(String className, Object object, String name, Object value)
            throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        getField(findClass(className), name).set(object, value);
    }

    /**
     * <p>
     * Tries to identify a field of class by the fields type. The first matching field will be returned.</p>
     *
     * @param clazz The class.
     * @param type The type of the field.
     *
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static Field getField(Class clazz, Class type)
            throws NoSuchFieldException {
        Field[][] all = new Field[][]{clazz.getDeclaredFields(), clazz.getFields()};
        for (Field[] array : all) {
            for (Field f : array) {
                if (f.getType() == type) {
                    f.setAccessible(true);
                    return f;
                }
            }
        }
        throw new NoSuchFieldException("Class \"" + clazz.getName() + "\" has no field of type " + type.getName() + "!");
    }

    /**
     * <p>
     * Returns a field of a class.</p>
     *
     * @param clazz The class.
     * @param name The name of the field.
     *
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    public static Field getField(Class clazz, String name)
            throws NoSuchFieldException {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            Field f = clazz.getField(name);
            f.setAccessible(true);
            return f;
        }
    }

    /**
     * <p>
     * Returns a new instance of a Minecraft class.</p>
     *
     * @param className The name of the Minecraft class.
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the constructor.
     *
     * @return A new instance of {@code className}.
     */
    public static Object instantiateMinecraft(String className, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return instantiate(getMinecraftPackage() + className, paramTypes, params);
    }

    /**
     * <p>
     * Returns a new instance of a Craftbukkit class.</p>
     *
     * @param className The name of the Craftbukkit class (<b>with</b> leading subpackages!).
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the constructor.
     *
     * @return A new instance of {@code className}.
     */
    public static Object instantiateCraftbukkit(String className, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return instantiate(getCraftbukkitPackage() + className, paramTypes, params);
    }

    /**
     * <p>
     * Returns a new instance of {@code className}.</p>
     *
     * @param className The full name of the class.
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the constructor.
     *
     * @return A new instance of {@code className}.
     */
    public static Object instantiate(String className, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        return getConstructor(findClass(className), parseClass(paramTypes)).newInstance(params);
    }

    /**
     * <p>
     * Returns the constructor of a class.</p>
     *
     * @param clazz The class.
     * @param paramTypes The classes of the parameters.
     *
     * @return The constructor of the class.
     */
    public static Constructor getConstructor(Class clazz, Class... paramTypes)
            throws NoSuchMethodException {
        try {
            Constructor c = clazz.getDeclaredConstructor(paramTypes);
            c.setAccessible(true);
            return c;
        } catch (NoSuchMethodException e) {
            Constructor c = clazz.getConstructor(paramTypes);
            c.setAccessible(true);
            return c;
        }
    }

    /**
     * <p>
     * Tries to invoke a method of a Minecraft class without knowing the methods name.</p>
     * <p>
     * Tries to identify a method of a Minecraft class by its return and parameter types. The first one found will be invoked.</p>
     * <p>
     * Example: (Given: {@code byte[] array;})</p>
     * <blockquote>{@code NBTLib.invokeMinecraftDynamic("NBTCompressedStreamTools", null, NBTLib.getMinecraftPackage() + "NBTTagCompound", new Object[]{byte[].class}, new Object[]{array});}</blockquote>
     *
     * @param className The name of the Minecraft class.
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param returnType The return type, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invokeMinecraftDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return invokeDynamic(getMinecraftPackage() + className, object, returnType, paramTypes, params);
    }

    /**
     * <p>
     * Invokes a method of a Minecraft class.</p>
     *
     * @param className The name of the Minecraft class.
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param name The name of the called method.
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types (classes and/or their names).
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invokeMinecraft(String className, Object object, String name, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return invoke(getMinecraftPackage() + className, object, name, paramTypes, params);
    }

    /**
     * <p>
     * Tries to invoke a method of a Craftbukkit class without knowing the methods name.</p>
     * <p>
     * Tries to identify a method of a Craftbukkit class by its return and parameter types. The first one found will be invoked.</p>
     * <p>
     * Example: (Given: {@code Object item;} but actually instance of {@code net.minecraft.server.v_1_4_6.ItemStack})</p>
     * <blockquote>{@code NBTLib.invokeCraftbukkit("inventory.CraftItemStack", null, "asBukkitCopy", new Object[]{NBTLib.getMinecraftPackage() + "ItemStack"}, new Object[]{item});}</blockquote>
     *
     * @param className The name of the Craftbukkit class (<b>with</b> leading subpackages!).
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param returnType The return type, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invokeCraftbukkitDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return invokeDynamic(getCraftbukkitPackage() + className, object, returnType, paramTypes, params);
    }

    /**
     * <p>
     * Invokes a method of a Craftbukkit class.</p>
     *
     * @param className The name of the Craftbukkit class.
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param name The name of the called method.
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types (classes and/or their names).
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invokeCraftbukkit(String className, Object object, String name, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NBTLibDisabledException {
        return invoke(getCraftbukkitPackage() + className, object, name, paramTypes, params);
    }

    /**
     * <p>
     * Tries to identify a method of a class by its return and parameter types. The first one found will be invoked.</p>
     *
     * @param className The full name of the class.
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param returnType The return type, must be an instance of either {@link Class} or {@link String} (the class or its name).
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types.
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invokeDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return getMethod(findClass(className), parseClass(returnType), parseClass(paramTypes)).invoke(object, params);
    }

    /**
     * <p>
     * Invokes a method of a class.</p>
     *
     * @param className The full name of the class.
     * @param object The instance on which the method should be invoked. Use {@code null} for static methods.
     * @param name The name of the called method.
     * @param paramTypes An array of {@link Class Classes} and/or {@link String Strings} representing the parameter types (classes and/or their names).
     * @param params The arguments to pass to the method.
     *
     * @return Whatever the invoked method returns.
     */
    public static Object invoke(String className, Object object, String name, Object[] paramTypes, Object... params)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return getMethod(findClass(className), name, parseClass(paramTypes)).invoke(object, params);
    }

    /**
     * <p>
     * Returns a method of a class. The method is identified by its return type and its parameter types. The first matching method will be returned.</p>
     *
     * @param clazz The class containing the method.
     * @param returnType The class of the returned object.
     * @param params The classes of the parameters.
     *
     * @return The method.
     */
    public static Method getMethod(Class clazz, Class returnType, Class... params)
            throws NoSuchMethodException {
        Method[][] all = new Method[][]{clazz.getDeclaredMethods(), clazz.getMethods()};
        for (Method[] array : all) {
            label1:
            for (Method m : array) {
                if (m.getReturnType() == returnType) {
                    Class[] args = m.getParameterTypes();
                    if (args.length != params.length) {
                        continue;
                    }
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != params[i]) {
                            continue label1;
                        }
                    }
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        String string = "Class \"" + clazz.getName() + "\" has no method (";
        boolean first = true;
        for (Class cls : params) {
            if (first) {
                first = false;
            } else {
                string += ", ";
            }
            string += cls.getName();
        }
        string += ") returning \"" + returnType.getName() + "\"!";
        throw new NoSuchMethodException(string);
    }

    /**
     * <p>
     * Returns a method of a class.</p>
     *
     * @param clazz The class containing the method.
     * @param name The name of the method.
     * @param params The classes of the parameters.
     *
     * @return The method.
     */
    public static Method getMethod(Class clazz, String name, Class... params)
            throws NoSuchMethodException {
        try {
            Method m = clazz.getDeclaredMethod(name, params);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            Method m = clazz.getMethod(name, params);
            m.setAccessible(true);
            return m;
        }
    }

    /**
     * <p>
     * Parses an array of {@link Class Classes} and/or {@link String Strings} to only Classes.</p>
     *
     * @param array An array of {@link Class Classes} and/or {@link String Strings}.
     *
     * @return An array of classes.
     */
    public static Class[] parseClass(Object... array)
            throws ClassNotFoundException {
        ArrayList<Class> list = new ArrayList<Class>();
        for (Object o : array) {
            list.add(parseClass(o));
        }
        return list.toArray(new Class[0]);
    }

    /**
     * <p>
     * Parses a {@link String} or {@link Class} to a Class.</p>
     *
     * @param o A {@link String} or {@link Class}.
     *
     * @return An array of classes.
     */
    public static Class parseClass(Object o)
            throws ClassNotFoundException {
        if (o instanceof Class) {
            return (Class) o;
        } else {
            return findClass((String) o);
        }
    }
}
