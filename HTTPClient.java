import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class HTTPClient {
  public static void main(String[] args) throws Exception {
  	String server_ip = "172.16.5.162";
  	String downloadFileName = "4K.jpg";
  	boolean autoflush = true;
    int i, count, end_of_header, start_content_length_field, end_content_length_field;
    long content_len, write_byte;
    String request_msg = "", s;
    FileOutputStream outFile= new FileOutputStream(downloadFileName);
	  byte[] buffer =new byte[1800];

    // Establish a TCP connection to the server
    InetAddress addr = InetAddress.getByName(server_ip);
    Socket socket = new Socket(addr, 80);

    // get input and output streams of the socket
    PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);    // output stream
    BufferedInputStream in = new BufferedInputStream(socket.getInputStream()); // input stream
    
    
	    // send an HTTP request to download file '4K.jpg' stored on the server
    
    	// prepare GET request message
    	request_msg += "GET /" + downloadFileName + " HTTP/1.1\r\n";
    	request_msg += "Host: " + server_ip + "\r\n";
    	request_msg += "Connection: keep-alive\r\n";
    	request_msg += "\r\n";

    	System.out.println("***GET message:");
	    System.out.println(request_msg);

    	// send request message via output stream (out.print())
	    out.print(request_msg);
	    out.flush();

	    
	    // read server's response via input stream (in.read) into a buffer
	    count = in.read(buffer);
	    s = new String(buffer);

	    // look for the end of headers
	    end_of_header = s.indexOf("\r\n\r\n") + 4;

	    // look for the 'Content-Length' field to find the data size (bytes)
	    start_content_length_field = s.indexOf("Content-Length: ");
	    end_content_length_field = s.substring(start_content_length_field).indexOf("\r\n") + start_content_length_field;
	    content_len = Long.parseLong(s.substring(start_content_length_field+16, end_content_length_field));
	    System.out.println("***Response's Header:\n" + s.substring(0, end_of_header));
	    
	    /* write image data to file */
	    write_byte = 0;
	    outFile.write(buffer,end_of_header,count - end_of_header);
	    write_byte += (count - end_of_header);
	    
	    while(write_byte < content_len){
	        count = in.read(buffer);
	        outFile.write(buffer, 0, count);
	        write_byte += count;
	    }
	    outFile.flush();
	    outFile.close();
	    System.out.println("Requested file saved to: " + downloadFileName);
    	
   socket.close();
  }
  
  
  
}