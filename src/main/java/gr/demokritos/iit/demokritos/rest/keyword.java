package gr.demokritos.iit.demokritos.rest;
//import java.io.Serializable;
public class keyword //implements Serializable
{
	public static int defaultLimit = 50;

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
		return String.format("%s-%s-%d",value, lang,limit);
	}
}