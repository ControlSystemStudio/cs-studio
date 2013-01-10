import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/** Read list of plugins from /tmp/<site name>,
 *  generate trac wiki table of which site uses what
 *  @author Kay Kasemir
 */
public class PluginUsers
{
	public static void main(String[] args) throws Exception
    {
		final Map<String, Set<String>> plugin_users = new HashMap<String, Set<String>>();
		final String[] sites = { "DESY", "KEK", "NSLS2", "SNS" };

		for (String site : sites)
		{
		    final BufferedReader reader = new BufferedReader(new FileReader("/tmp/" + site));
		    String plugin = reader.readLine();
		    while (plugin != null)
		    {
		    	// Patch plugin/**
		    	int sep = plugin.indexOf('/');
		    	if (sep > 0)
		    		plugin = plugin.substring(0, sep);
		    	// Patch plugin_3.5.4.....jar
	    		sep = plugin.indexOf('_');
		    	if (sep > 0)
		    		plugin = plugin.substring(0, sep);
		    	Set<String> users = plugin_users.get(plugin);
		    	if (users == null)
		    	{
		    		users = new HashSet<String>();
		    		plugin_users.put(plugin, users);
		    	}
		    	users.add(site);
		    	
		    	// System.out.println(site + " -> " + plugin);
		    	plugin = reader.readLine();
		    }
		    reader.close();
		}
		
		final String[] plugins = plugin_users.keySet().toArray(new String[0]);
		Arrays.sort(plugins);

		System.out.print("||'''Plugin'''||");
		for (String site : sites)
			System.out.print("'''" + site + "'''||");
		System.out.println();

		for (String plugin : plugins)
		{
			if (plugin.startsWith("org.eclipse") ||
			    plugin.startsWith("com.") ||
			    plugin.startsWith("javax.") ||
			    plugin.startsWith("org.apache"))
				continue;
			final Set<String> users = plugin_users.get(plugin);
			System.out.print("||" + plugin + "||");
			for (String site : sites)
				if (users.contains(site))
					System.out.print(site + "||");
				else
					System.out.print(" ||");
			System.out.println();
		}
    }
}
