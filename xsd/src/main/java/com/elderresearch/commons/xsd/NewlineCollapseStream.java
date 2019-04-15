package com.elderresearch.commons.xsd;

import java.io.IOException;
import java.io.OutputStream;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
class NewlineCollapseStream extends OutputStream {
	private final OutputStream delegate;
	
	private int consecutiveNewlines;
	
	@Override
	public void write(int b) throws IOException {
		val isNewline    = b == '\n';
		val isWhitespace = Character.isWhitespace(b);
		
		if (isNewline) {
			consecutiveNewlines++;
		} else if (isWhitespace) {
			// Neutral
		} else {
			consecutiveNewlines = 0;
		}
		
		if (consecutiveNewlines < 3) {
			delegate.write(b);
		}
	}
	
	@Override
	public void close() throws IOException {
		delegate.close();
	}
}
