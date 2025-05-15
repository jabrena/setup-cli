package info.jab.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

public class GitInfoPrinter {

    private final Supplier<InputStream> gitPropertiesStreamSupplier;

    public GitInfoPrinter() {
        this(() -> GitInfoPrinter.class.getClassLoader().getResourceAsStream("git.properties"));
    }

    GitInfoPrinter(Supplier<InputStream> gitPropertiesStreamSupplier) {
        this.gitPropertiesStreamSupplier = gitPropertiesStreamSupplier;
    }

    public void printGitInfo() {
        try (InputStream input = gitPropertiesStreamSupplier.get()) {
            //Preconditions
            if (Objects.isNull(input)) {
                System.out.println("git.properties not found");
                return;
            }

            //Load properties
            Properties prop = new Properties();
            prop.load(input);

            //Print info
            System.out.print(colorize("Version: ", Attribute.GREEN_TEXT()));
            System.out.println(prop.getProperty("git.build.version"));
            System.out.print(colorize("Commit: ", Attribute.GREEN_TEXT()));
            System.out.println(prop.getProperty("git.commit.id.abbrev"));
            System.out.println();
        } catch (IOException ex) {
            System.out.println("Error printing git info: " + ex.getMessage());
        }
    }
}
