package fr.hashtek.hashconfig;

import fr.hashtek.hashconfig.exception.InstanceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.snakeyaml.lib.Yaml;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;


public class HashConfig
{

    private static HashConfig instance = null;

    private final Class<?> plugin;
    private Dotenv env = null;
    private YamlFile yaml;
    private final String resourcePath;
    private final String outputPath;

    /**
     * @param resourcePath The ABSOLUTE path of the configuration file to load. !! WARNING !! The file must be present in the package (PluginName.jar).
     * @throws IOException If the plugin can't read the file
     */
    public HashConfig(Class<?> plugin, String resourcePath, String outputPath, boolean withDotEnv) throws IOException
    {
        HashConfig.instance = this;
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.outputPath = outputPath;
        this.load(withDotEnv);
    }


    /**
     * @throws IOException If the configuration failed to load.
     */
    public void reload() throws IOException
    {
        this.load(this.env != null);
    }

    /**
     * @throws IOException If the configuration has failed to save.
     */
    public void save() throws IOException
    {
        this.yaml.save(this.outputPath);
    }

    /**
     * Load a yaml file to an entity object.
     *
     * @return the entity loaded.
     * @throws IOException if the input stream failed to close.
     */
    public Class<?> loadToEntity() throws IOException
    {
        final Yaml yaml = new Yaml();
        final InputStream inputStream = this.plugin
            .getClassLoader()
            .getResourceAsStream(this.resourcePath);
        final Class<?> entity;

        if (inputStream == null)
            throw new NullPointerException("Cannot get resource \"" + this.resourcePath + "\" as stream.");
        entity = yaml.load(inputStream);
        inputStream.close();
        return entity;
    }

    private void load(boolean withDotEnv) throws IOException
    {
        File configFile = this.createFileIfNotExists();

        this.yaml = new YamlFile(configFile);
        this.yaml.loadWithComments();
        if (withDotEnv)
            this.env = Dotenv.load();
        else
            this.env = null;
    }

    private File createFileIfNotExists() throws IOException
    {
        File configFile = new File(this.outputPath);
        InputStream stream = null;

        if (configFile.exists())
            return configFile;

        if (!configFile.getParentFile().mkdirs() && !configFile.createNewFile())
            throw new IOException("Cannot create the default configuration file '"
                                  + configFile.getName()
                                  + "'.");

        stream = this.plugin.getResourceAsStream("/" + this.resourcePath);
        if (stream == null)
            throw new IOException("The resource file '"
                                  + this.resourcePath
                                  + "' cannot not be found in the jar file.");

        writeStreamToFile(stream, configFile);

        stream.close();
        return configFile;
    }

    private void writeStreamToFile(InputStream stream, File file) throws IOException
    {
        FileWriter writer = new FileWriter(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        BufferedWriter writerBuffer = new BufferedWriter(writer);
        String line = null;

        do {
            line = reader.readLine();

            if (line != null) {
                System.out.println("Writing line: '" + line + "'.");
                writerBuffer.write(line);
                writerBuffer.newLine();
            }
        } while (line != null);

        writerBuffer.close();
        writer.close();
        reader.close();
    }


    /**
     * @throws InstanceNotFoundException If there is no instance available.
     * @return The last instance created. !! WARNING !! If you create multiple instance of
     *           ConfigManager, then it returns only the last instance created.
     */
    public static HashConfig getInstance() throws InstanceNotFoundException
    {
        if (instance == null)
            throw new InstanceNotFoundException("The instance cannot be found.");
        return instance;
    }

    /**
     * @return The last instance YAML configuration. !! WARNING !! If you create multiple instance
     *           of ConfigManager, then it returns only the last instance YAML configuration.
     */
    public YamlFile getYaml()
    {
        return this.yaml;
    }

    /**
     * @return The last instance Dotenv.
     */
    public Dotenv getEnv()
    {
        return this.env;
    }

}
