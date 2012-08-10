package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="products")
public class Product extends Model {

	public String name;
	
	public BigDecimal price;
	
	@Column(name="expired_at")
	public Date expiredAt;
}
