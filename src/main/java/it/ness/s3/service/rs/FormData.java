package it.ness.s3.service.rs;


import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class FormData {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;

    @FormParam("mime_type")
    @PartType(MediaType.TEXT_PLAIN)
    public String mimeType;

    @FormParam("ldap_operation_type")
    @PartType(MediaType.TEXT_PLAIN)
    public String ldapOperationType;
}
