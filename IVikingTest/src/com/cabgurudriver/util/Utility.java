package com.cabgurudriver.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.xml.sax.InputSource;

//import vn.com.tma.mobile.service.LogService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Utility
{
	String TAG = "Utility";
	
	public InputStream OpenHttpConnection(String urlString) throws IOException
	{
		InputStream in = null;
		int response = -1;		
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		HttpURLConnection httpConn =null;
		System.setProperty("http.keepAlive", "false"); 
			try {
				httpConn = (HttpURLConnection) conn;				
				httpConn.connect();					
				response = httpConn.getResponseCode();								
				if (response == HttpURLConnection.HTTP_OK)
				{
					in = httpConn.getInputStream();						
				}								
			
			}catch (Exception ex) {
				Log.e("Error ", ex.getMessage());
				//throw new IOException("Error connecting");
			}
			return in; 
	}
	
	public InputSource retrieveInputStream(HttpEntity httpEntity) 
	{
		InputSource insrc = null;
        try {
        	insrc = new InputSource(httpEntity.getContent());
        } catch (Exception e) {
        	Log.e("Utility : ","Exception in retrieveInputStream():"+e.getMessage());
        }
        return insrc;
	 } 	
	// Begin - new Bitmap downloader - by Long Phan 05/09/2011
	public Bitmap DownloadImage(String imageURL)
	{        
		
		Bitmap img = null;		
		try {			
			InputStream is = OpenHttpConnection(imageURL);
			img = BitmapFactory.decodeStream(is);
			
			if (img == null){
				Log.d("Uitility : ","Image Null");
			}		
			
			is.close();
			Log.d("Utility : ","Download image "+ imageURL + " complete");
		} catch (IOException e) {
			Log.e("Utility : ","Exception in DownloadImage(URL) : "+ e.getMessage());
		} catch (Exception ex){
			Log.e("Utility : ","Unknown Exception in DownloadImage(URL) : "+ ex.getMessage());
		}
	
		System.gc();
		return img;                
	}
	
	
	public String imageSave(String path, String imageURL)   {
		String currentPath="";
		if(!imageURL.trim().equals("")){
			try{

				URL url = new URL(imageURL);
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.d("MSCN - imageSave",imageURL + " Lenght of file: " + lenghtOfFile);

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(path);

				byte data[] = new byte[1024];
				int count;
				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				currentPath=path;	
				
			}
			catch(Exception e){
				Log.e("MSCN",e.getMessage());
			}
		}
		return currentPath;
	}
	
	public boolean imageDelete(String filepath){
		
		File file = new File(filepath);
		return file.delete();
		
		
	}
	
	static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

	
	private void DEBUG(String msg){
		Log.i(TAG, msg);
	}
}
