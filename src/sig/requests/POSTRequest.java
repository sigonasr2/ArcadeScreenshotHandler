package sig.requests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLSession;

import sig.MultipartUtility;
import sig.exceptions.FailedResponseException;

public class POSTRequest extends GETRequest{

    String body = "";
    Path uploadFile = null;

    public POSTRequest(String url, String body, String username, String password, long timeout, Path outputFile, String... headers) {
        super(url, username, password, timeout, outputFile, headers);
        this.body=body;
    }

    public POSTRequest(String url, String body, long timeout, Path outputFile, String... headers) {
        super(url, timeout, outputFile, headers);
        this.body=body;
    }

    public POSTRequest(String url, String body, long timeout, String... headers) {
        super(url, timeout, headers);
        this.body=body;
    }

    public POSTRequest(String url, String body) {
        super(url);
        this.body=body;
    }

    public POSTRequest(String url, Path uploadFile) {
        super(url);
        this.uploadFile=uploadFile;
    }
    @Override
    public HttpResponse<?> run() throws FailedResponseException {
        if (uploadFile!=null) {
            String charset = "ISO-8859-1";
            File file = uploadFile.toFile();
            String requestURL = url;
     
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                 
                multipart.addHeaderField("User-Agent", "SIG HTTPCLIENT");
                 
                multipart.addFilePart("fileUpload", file);
     
                List<String> response = multipart.finish();
                return new HttpResponse<String>(){
                    @Override
                    public int statusCode() {
                        return 0;
                    }
                    @Override
                    public HttpRequest request() {
                        return null;
                    }
                    @Override
                    public Optional<HttpResponse<String>> previousResponse() {
                        return null;
                    }
                    @Override
                    public HttpHeaders headers() {
                        return null;
                    }
                    @Override
                    public String body() {
                        StringBuilder sb = new StringBuilder();
                        for (String s : response) {
                            sb.append(s);
                        }
                        return sb.toString();
                    }
                    @Override
                    public Optional<SSLSession> sslSession() {
                        return null;
                    }
                    @Override
                    public URI uri() {
                        return null;
                    }
                    @Override
                    public Version version() {
                        return null;
                    }
                };
            } catch (IOException ex) {
                System.err.println(ex);
                throw new FailedResponseException("Could not send response for POST File upload. THIS SHOULD NOT BE HAPPENING!");
            }
        } else {
            return super.run();
        }
    }
    @Override
    protected java.net.http.HttpRequest.Builder finalizeRequestPreBuild(
            java.net.http.HttpRequest.Builder requestBuild) throws FailedResponseException{
                requestBuild.headers("Content-Type","application/json");
        try {
            return file!=null?requestBuild.POST(HttpRequest.BodyPublishers.ofFile(file)):
            body.length()>0?requestBuild.POST(HttpRequest.BodyPublishers.ofString(body)):
            requestBuild.POST(HttpRequest.BodyPublishers.noBody());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new FailedResponseException("Could not complete request build. THIS SHOULD NOT BE HAPPENING!");
    }

    @Override
    protected Builder finalizeClientPreBuild(Builder clientBuild) {
        return clientBuild;
    }
    
}