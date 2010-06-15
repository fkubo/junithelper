package org.junithelper.plugin.util;

import static org.mockito.Mockito.mock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.junithelper.plugin.exception.InvalidPreferenceException;

public class FileResourceUtilTest extends TestCase {

	public void test_close_A$BufferedInputStream() throws Exception {
		BufferedInputStream bis = null;
		FileResourceUtil.close(bis);
	}

	public void test_close_A$BufferedReader() throws Exception {
		BufferedReader br = null;
		FileResourceUtil.close(br);
	}

	public void test_close_A$FileOutputStream() throws Exception {
		FileOutputStream fos = null;
		FileResourceUtil.close(fos);
	}

	public void test_close_A$InputStream() throws Exception {
		InputStream is = null;
		FileResourceUtil.close(is);
	}

	public void test_close_A$InputStreamReader() throws Exception {
		InputStreamReader isr = null;
		FileResourceUtil.close(isr);
	}

	public void test_close_A$OutputStreamWriter() throws Exception {
		OutputStreamWriter osw = null;
		FileResourceUtil.close(osw);
	}

	public void test_readFile_A$IFile() throws Exception {
		// given
		// e.g. : given(mocked.called()).willReturn(1);
		IFile file = mock(IFile.class);
		// when
		InputStream actual = FileResourceUtil.readFile(file);
		// then
		// e.g. : verify(mocked).called();
		InputStream expected = null;
		assertEquals(expected, actual);
	}

	public void test_readFile_A$IFile_T$InvalidPreferenceException()
			throws Exception {
		// given
		IFile arg0 = null;
		// when
		try {
			FileResourceUtil.readFile(arg0);
			fail("Expected exception was not thrown! (InvalidPreferenceException)");
		} catch (InvalidPreferenceException e) {
		}
	}

	public void test_detectEncoding_A$IFile() throws Exception {
		// given
		IFile file = mock(IFile.class);
		// when
		String actual = FileResourceUtil.detectEncoding(file);
		// then
		String expected = "UTF-8";
		assertEquals(expected, actual);
	}

}
