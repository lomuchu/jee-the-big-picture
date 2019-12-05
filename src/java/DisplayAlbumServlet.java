/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author Quarksoft
 */
@WebServlet(name = "DisplayAlbumServlet",
        urlPatterns = {"/DisplayAlbumServlet"})
@MultipartConfig()
public class DisplayAlbumServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext servletContext = request.getServletContext();
        PhotoAlbum pa = PhotoAlbum.getPhotoAlbum(servletContext);
        if(request.getContentType() != null && 
                request.getContentType().startsWith("multipart/form-data")){
            this.uploadPhoto(request, pa);
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Photo Viewer</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h3 align='center'>Photos</h3>");
            this.displayAlbum(pa, "", out);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
   
    private void uploadPhoto(HttpServletRequest request, PhotoAlbum pa)
            throws ServletException, IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String filename = null;
        for (Part p : request.getParts()) {
            this.copyBytes(p.getInputStream(), baos);
            filename = p.getSubmittedFileName();
        }
        if(!"".equals(filename)){
            String photoName = filename.substring(0, filename.lastIndexOf("."));
            pa.addPhoto(photoName, baos.toByteArray());
        }
    }
    
    private void displayAlbum(PhotoAlbum pa,
                               String label,
                               PrintWriter writer){
        writer.write("<h3 align='center'>"+label+"</h3>");
        writer.write("<table align='center'>");
        for (int j = 0; j < pa.getPhotoCount(); j++) {
            writer.write("<td>");
            writer.write("<a href='./DisplayPhotoServlet?photo="+j+"'>");
            writer.write("<img src='./DisplayPhotoServlet?photo="+j+"' alt='photo' height='120' width='150'>");
            writer.write("</a>");
            writer.write("</td>");
        }
        
        writer.write("<td bgcolor='#cccccc' width='120' height='120'>");
        writer.write("<form align='left' action='DisplayAlbumServlet' method='post' enctype='multipart/form-data'>");
        writer.write("<input value='Choose' name='myFile' type='file' accept='image/jpeg'><br>");
        writer.write("<input value='Upload' type='submit\'><br>");
        writer.write("</form>");
        writer.write("</td>");
        writer.write("</tr>");
        
        writer.write("<tr>");
        for (int j = 0; j < pa.getPhotoCount(); j++) {
            writer.write("<td align='center'>");
            writer.write(pa.getPhotoName(j));
            writer.write("</td>");
        }
        writer.write("</tr>");
        
        writer.write("<tr>");
        for (int j = 0; j < pa.getPhotoCount(); j++) {
            writer.write("<td align='center'>");
            writer.write("<a href='RemovePhotoServlet?photo="+j+"'>remove</a>");
            writer.write("</td>");
        }
        writer.write("</tr>");
        writer.write("</table>");
    }
    
    private void copyBytes(InputStream is, OutputStream os)
                                                 throws IOException{
        int i;
        while ( (i=is.read()) != -1) {
            os.write(i);
        }
        is.close();
        os.close();
    }
}
