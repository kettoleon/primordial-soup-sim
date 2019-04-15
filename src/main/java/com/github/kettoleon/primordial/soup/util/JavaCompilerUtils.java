package com.github.kettoleon.primordial.soup.util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JavaCompilerUtils {


    public static <T> T instantiateSourceCode(String className, String sourceCode) {
        try {
            Path javaFile = saveSource(className, sourceCode);
            Path classFile = compileSource(className, javaFile);
            return instantiate(className, classFile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Path saveSource(String className, String source) throws IOException {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path sourcePath = Paths.get(tmpProperty, className + ".java");
        Files.write(sourcePath, source.getBytes(UTF_8));
        return sourcePath;
    }

    private static Path compileSource(String className, Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
        return javaFile.getParent().resolve(className + ".class");
    }

    private static <T> T instantiate(String className, Path javaClass)
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        URL classUrl = javaClass.getParent().toFile().toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
        Class<?> clazz = Class.forName(className, true, classLoader);
        return (T) clazz.newInstance();
    }

    private static int count;

    public static String uniqueClassNameSuffix(String digitalBrainAlgorithm) {
        return digitalBrainAlgorithm + count++;
    }
}
