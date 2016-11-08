
package gr.demokritos.iit.demokritos.rest;

public class StringObj extends Parsable
{
//	public static String id = "str";
	private String value;
	public StringObj(){}
    public StringObj(String value){ this.value = value; }

	public void setValue(String a){ value=a;}
	public String getValue(){ return value;}

	@Override
	public String toString() {
		return String.format("%s",value);
	}
	@Override
	public void fromString(String s)
	{
		value = s;
	}
}