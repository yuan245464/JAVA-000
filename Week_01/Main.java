import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;



public class Main {

	public static void main(String[] args) throws Exception {
		
		URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:/D:/dev/EclipseWorkspace/LoadCustClass/files/")});
		invokeHelloMethod(loader);
		loader.close();
		
		XClassLoader xLoader = new XClassLoader("file:/D:/dev/EclipseWorkspace/LoadCustClass/files/");
		invokeHelloMethod(xLoader);
		loader.close();
	}

	private static void invokeHelloMethod(ClassLoader loader) throws Exception {
		Class<?> helloClass = loader.loadClass("Hello");
		Method helloMethod = helloClass.getDeclaredMethod("hello", null);
		helloMethod.invoke(helloClass.newInstance(), null);
	}

}

class XClassLoader extends ClassLoader {

	private static final int FACTOR = 255;
	
	private String classpath;
	
	public XClassLoader(String classpath) {
		this.classpath = classpath;
	}

	protected Class<?> findClass(final String name) throws ClassNotFoundException  {
		try {
			String path = name.replace('.', '/').concat(".xlass");
			URL url = new URL(classpath + path);
			URLConnection conn = url.openConnection();
			conn.connect();
			int size = conn.getContentLength();
			InputStream inputStream = conn.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] bytes = new byte[size];
			
			int len = 0;
			do {
				int n = bufferedInputStream.read(bytes, len, size - len);
				if (n < 0) {
					throw new RuntimeException("Error");
				}
				len += n;
			} while (len < size);
			bufferedInputStream.close();
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] =  (byte)(FACTOR - bytes[i]);
			}
            return defineClass(null, bytes, 0, size, null);
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage());
		}
	}

}