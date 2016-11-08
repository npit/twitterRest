package gr.demokritos.iit.demokritos.rest;
//import java.io.Serializable;
public class keyword  extends Parsable
{
//    public static String id = "kw";
	private int limit;
	private String value, lang;
	public keyword(){}
    public keyword(String value){ this.value = value; }
    public keyword(String value,int lim){ this.value = value; this.limit = lim; }
    public keyword(String value,int lim,String Lang){ this.value = value; this.limit = lim;  this.lang = Lang;}

	public void setValue(String a){ value=a;}
	public String getValue(){ return value;}

	public void setLimit(int a){ limit =a;}
	public int getLimit(){ return limit;}

    public String getLang(){ return lang;}
    public void setLang(String l){ lang = l; }

	@Override
	public String toString() {
		return String.format("%s%s%s%s%d",value, delimiter,lang,delimiter,limit);
	}

	@Override
	public void fromString(String s) throws InstantiationException
	{
		String[] vals = s.split("[*]{3}");
		if (vals.length < 3){
			throw new InstantiationException("Keyword components number is " + vals.length + " , 3 expected.");
		}

		value = vals[0];
		lang = vals[2];
		limit = Integer.parseInt(vals[2]);
	}

}