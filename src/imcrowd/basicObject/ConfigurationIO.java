package imcrowd.basicObject;

import java.util.Map;
import java.util.Properties;

public interface ConfigurationIO {
	public Map<String, String> getAttributes(int i);
	public void setAttributes(Properties configuration, int i);
}
