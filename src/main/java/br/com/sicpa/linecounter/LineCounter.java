package br.com.sicpa.linecounter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class LineCounter implements Runnable{

	
	private File f;
	private long totalLines;
	
	public LineCounter(File f, FileVisitorImpl fileVisitor) {
		this.f = f;
		this.fileVisitor = fileVisitor;
	}

	
	public void setTotalLines(long totalLines) {
		this.totalLines = totalLines;
	}

	public long getTotalLines() {
		return totalLines;
	}


	@Override
	public void run() {
		try {
			Files.walkFileTree(f.toPath(), fileVisitor);
			setTotalLines(fileVisitor.count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LineCounter lineCounter = new LineCounter(new File(args[0]), new FileVisitorImpl());
		Thread t = new Thread(lineCounter);
		t.start();
		try {
			t.join();
			System.out.println(lineCounter.getTotalLines());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	FileVisitorImpl fileVisitor;

}

class FileVisitorImpl implements FileVisitor<Path> {

	long count =0;
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		
		List<String> list = Files.readAllLines(file,Charset.defaultCharset() );
		for (String line : list){
			if( !line.trim().equals(""))
				count++;
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
}