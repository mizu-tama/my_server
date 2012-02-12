package com.gmail.mizuno.tamaki;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyServer {
	private static final Pattern REQUEST_PATTERN = Pattern.compile("^(\\w+)\\s+(.+)\\s+HTTP/([0-9\\.]+)$");
	private static final int PORT = 8000;

	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(PORT);
			while (true) {
				Socket client = server.accept();
				run(client);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void run(Socket client) {
//		String method;
		String path = null;
		String version;

		BufferedReader req = null;
		BufferedWriter res = null;
		try {
			//リクエストの読込み
			//TODO Request class にまとめる
			req = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String line = req.readLine();
			System.out.println(line);
			Matcher m = REQUEST_PATTERN.matcher(line);
			if (m.matches()) {
//				method = m.group(1);
				path = m.group(2);
				version = m.group(3);
			} else {
				//TODO throw new BadRequestsException
			}
			//メッセージヘッダの処理。とりあえず出力するだけ。
			while (req.ready() && (line = req.readLine()) != null) {
				System.out.println(line);
			}

			//レスポンスを返す
			res = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			if (path == null) {
				//TODO throw new BadRequestsException
			}
			if (path == "/") {
				path = "/index.html";
			}
			String resFile = "." + path;
			File file = new File(resFile);
			if (!file.exists()) {
				//404
				res.flush();
				return;
			}
			FileInputStream fileInStream = new FileInputStream(file);
			
			res.write("HTTP/1.1 200 OK\n");
			res.write("Content-Type: text/plain; charset=utf-8\n");
			res.write("\n");
			sendFile(res, fileInStream);
			res.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (req != null) {
				try {
					req.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (res != null) {
				try {
					res.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void sendFile(BufferedWriter res, FileInputStream fileInStream) {
		// TODO Auto-generated method stub
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileInStream));
		try {
			String line;
			while (fileReader.ready() && (line = fileReader.readLine()) != null) {
				res.write(line);
//				System.out.println(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
