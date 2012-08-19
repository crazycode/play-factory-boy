package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "products")
public class Product extends Model {

    public String name;

    public BigDecimal price;

    @Column(name = "expired_at")
    public Date expiredAt;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "categories_products",
                    inverseJoinColumns = @JoinColumn(name = "category_id"),
                    joinColumns = @JoinColumn(name = "product_id"))
    public Set<Category> categories;

}
