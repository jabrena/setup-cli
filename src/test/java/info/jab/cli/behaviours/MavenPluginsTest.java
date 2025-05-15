package info.jab.cli.behaviours;

import info.jab.cli.io.UpdateMavenPom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MavenPluginsTest {

    @SuppressWarnings("NullAway")
    @Mock
    private UpdateMavenPom updateMavenPom;

    @SuppressWarnings("NullAway")
    @InjectMocks
    private MavenPlugins mavenPlugins;

    @Test
    void should_execute_flatten_logic_when_parameter_is_flatten() {
        //Given
        String parameter = "flatten";
        String pomPath = "pom.xml";
        String flattenPluginXmlPath = "maven-plugins/maven-plugins-flatten.xml";
        String propertiesXmlPath = "maven-plugins/maven-plugins-flatten-version.xml";

        //When
        mavenPlugins.execute(parameter);

        //Then
        verify(updateMavenPom, times(1)).writePluginInBuildSection(pomPath, flattenPluginXmlPath);
        verify(updateMavenPom, times(1)).writeProperties(pomPath, propertiesXmlPath);
    }

    @Test
    void should_not_execute_any_logic_when_parameter_is_not_flatten() {
        //Given
        String parameter = "unknown";

        //When
        mavenPlugins.execute(parameter);

        //Then
        verifyNoInteractions(updateMavenPom);
    }

    @SuppressWarnings("NullAway")
    @Test
    void should_not_execute_any_logic_when_parameter_is_null() {
        //Given
        String parameter = null;

        //When
        mavenPlugins.execute(parameter);

        //Then
        verifyNoInteractions(updateMavenPom);
    }
}
