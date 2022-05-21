package sig.requests;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Duration;

import sig.exceptions.FailedResponseException;

public class GETRequest{
    String url;
    String[] headers;
    long timeout;
    Path file;
    String user="";
    String pass="";
    private HttpRequest req;
    private HttpClient client;
    /**
     * @param file The file path info, use this for file downloads or set to null for standard text.
     * @param timeout in milliseconds
     * */
    public GETRequest(String url, String username, String password, long timeout, Path file, String...headers){
        this.url = url;
        this.user=username;
        this.pass=password;
        this.headers = headers;
        this.timeout = timeout;
        this.file=file;
    }
    /**
     * @param file The file path info, use this for file downloads or set to null for standard text.
     * @param timeout in milliseconds
     * */
    public GETRequest(String url, long timeout, Path file, String...headers){
        this(url,"","",timeout,file,headers);
    }
    /**
     * @param timeout in milliseconds
     * */
    public GETRequest(String url, long timeout, String...headers){
        this(url,timeout,null,headers);
    }
    public GETRequest(String url){
        this(url,30000,null);
    }
    public HttpResponse<?> run() throws FailedResponseException {
        build();
        try {
            if (file==null) {
                return client.send(req,BodyHandlers.ofString());
            } else {
                return client.send(req,BodyHandlers.ofFile(file));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new FailedResponseException("No proper response returned. THIS SHOULD NOT BE HAPPENING!");
    }
    protected java.net.http.HttpRequest.Builder finalizeRequestPreBuild(java.net.http.HttpRequest.Builder requestBuild) throws FailedResponseException {
        return requestBuild.GET();
    }
    protected Builder finalizeClientPreBuild(Builder clientBuild) {
        return clientBuild;
    }
    protected void build(){
        boolean AUTH_REQUIRED=user.length()>0&&pass.length()>0;
        try {
            java.net.http.HttpRequest.Builder requestBuild=HttpRequest.newBuilder(new URI(url))
            .version(HttpClient.Version.HTTP_2)
            .timeout(Duration.ofMillis(timeout));
            if (headers!=null&&headers.length>0) {
                requestBuild.headers(headers);
            }
            req = finalizeRequestPreBuild(requestBuild).build();
            Builder clientBuild=HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS);
            if (AUTH_REQUIRED) {
                clientBuild.authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user,pass.toCharArray());
                    }
                });
            }
            client = finalizeClientPreBuild(clientBuild).build();
        } catch (URISyntaxException | FailedResponseException e) {
            e.printStackTrace();
        }
    }
}