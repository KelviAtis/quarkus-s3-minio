package it.ness.s3.service.rs;

import io.minio.*;
import io.minio.errors.*;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Path("/s3-minio")
@ApplicationScoped
public class GreetingResource {


    @Inject
    S3Client s3Client;


    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;


    @Startup
    public void init() {
        System.out.println("INIT VERIFY BUCKET");
        try {
            s3Client.verifyBucket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/upload")
    public Response upload() throws Exception {
        String fileName = s3Client.path("prova.txt");
        String dummyFile = "Dummy content";
        String mimeType = "text/html";
        return Response.ok(
                s3Client.uploadObject(fileName, new ByteArrayInputStream(dummyFile.getBytes()), mimeType)
        ).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    @Transactional
    public Response upload(@MultipartForm FormData formData) {
        try {
            String uuid = UUID.randomUUID().toString();
            String path = s3Client.path(uuid);
            s3Client.uploadObject(path, formData.data, formData.mimeType);
            return Response.ok(uuid).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

    }

    @GET
    @Path("/download")
    public Response download() throws Exception {
        String fileName = s3Client.path("prova.txt");
        String mimeType = "text/html";
        return Response.ok(s3Client.downloadObject(fileName), mimeType)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }

    @GET
    @Path("/download/{uuid}/file")
    public Response downloadFile(@PathParam("uuid") String uuid, @QueryParam("mimeType") String mimeType) throws Exception {
        String fileName = s3Client.path(uuid);
        if(mimeType != null){
            return Response.ok(s3Client.downloadObject(fileName), mimeType)
                    .header("Content-Disposition", "attachment; filename=\"" + uuid + "\"")
                    .build();
        }
        return Response.ok(s3Client.downloadObject(fileName))
                .header("Content-Disposition", "attachment; filename=\"" + uuid + "\"")
                .build();
    }
}
