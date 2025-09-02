package info.jab.cli.behaviours;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.jspecify.annotations.NonNull;

import info.jab.cli.io.GitFolderCopy;
import io.vavr.control.Either;

public class Cursor implements Behaviour2, Behaviour3 {

    private final GitFolderCopy gitFolderCopy;

    public Cursor() {
        this.gitFolderCopy = new GitFolderCopy();
    }

    Cursor(GitFolderCopy gitFolderCopy) {
        this.gitFolderCopy = gitFolderCopy;
    }

    @Override
    public Either<String, String> execute(@NonNull String parameter1, @NonNull String parameter2) {
        return execute(parameter1, parameter2, ".cursor/rules");
    }

    @Override
    public Either<String, String> execute(@NonNull String parameter1, @NonNull String parameter2, @NonNull String parameter3) {
        String gitRepoUrl = parameter1;
        String sourceFolderPath = parameter2;
        String destinationPath = parameter3;

        // Validate the URL before proceeding
        Either<String, String> urlValidation = validateGitUrl(gitRepoUrl);
        if (urlValidation.isLeft()) {
            return urlValidation;
        }

        // Use trimmed URL for consistency with validation
        return executeWithOption(gitRepoUrl.trim(), sourceFolderPath, destinationPath);
    }

    /**
     * Validates if the given string is a valid URL and appears to be a git repository URL.
     *
     * @param urlString the URL string to validate
     * @return Either.left with error message if invalid, Either.right with success message if valid
     */
    private Either<String, String> validateGitUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return Either.left("Git repository URL cannot be null or empty");
        }

        try {
            // Use URI.create() and then convert to URL to avoid deprecated constructor
            URI uri = URI.create(urlString.trim());
            URL url = uri.toURL();

            // Check if protocol is supported for git operations
            // Note: git:// protocol is not supported by Java's URL class
            String protocol = url.getProtocol().toLowerCase(Locale.ENGLISH);
            if (!protocol.equals("http") && !protocol.equals("https")) {
                return Either.left("Unsupported protocol: " + protocol + ". Only http and https protocols are supported");
            }

            // Check if host is present
            if (url.getHost() == null || url.getHost().trim().isEmpty()) {
                return Either.left("Invalid URL: missing host");
            }

            return Either.right("Valid git repository URL");

        } catch (IllegalArgumentException e) {
            return Either.left("Invalid URI format: " + e.getMessage());
        } catch (MalformedURLException e) {
            return Either.left("Invalid URL format: " + e.getMessage());
        }
    }

    private Either<String, String> executeWithOption(String url, String sourceFolderPath, String destinationPath) {
        //Location where the cursor rules will be copied
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path finalDestinationPath = currentPath.resolve(destinationPath);

        gitFolderCopy.copyFolderFromRepo(url, sourceFolderPath, finalDestinationPath.toString());

        return Either.right("Cursor rules added successfully");
    }
}
