package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "orders")
public class Order extends Model {

	public String consignee;
	
	public String address;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @OrderBy("id")
	public List<OrderItem> orderItems;
}
