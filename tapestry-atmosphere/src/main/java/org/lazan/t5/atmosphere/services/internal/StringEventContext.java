package org.lazan.t5.atmosphere.services.internal;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.services.ValueEncoderSource;

public class StringEventContext implements EventContext {
	private final ValueEncoderSource valueEncoderSource;
	private final String[] strings;
	
	public StringEventContext(ValueEncoderSource valueEncoderSource,
			String[] strings) {
		super();
		this.valueEncoderSource = valueEncoderSource;
		this.strings = strings;
	}
	
	@Override
	public <T> T get(Class<T> type, int index) {
		return valueEncoderSource.getValueEncoder(type).toValue(strings[index]);
	}
	
	@Override
	public int getCount() {
		return strings == null ? 0 : strings.length;
	}
	
	@Override
	public String[] toStrings() {
		return strings;
	}
}