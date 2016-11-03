package gr.demokritos.iit.demokritos.rest;
//import java.io.Serializable;
public class keyword //implements Serializable
{
//	private int id;
	private String value;
	public keyword(){};
//	public int getId(){ return id;}
//	public void setId(int id){ this.id=id;}
	public void setValue(String a){ value=a;}
	public String getValue(){ return value;}
//	@Override
//	public String toString() {
//	return String.format("id:%d,keywords:%s",id,value);
//	}

	@Override
	public String toString() {
		return String.format("id:%d,keywords:%s",value);
	}
}