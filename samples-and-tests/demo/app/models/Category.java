package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "categories")
public class Category extends Model {

    public String name;

    @OrderColumn(name = "display_order")
    public Integer displayOrder;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent_id", nullable = true)
    public Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, targetEntity = Category.class)
    @OrderBy("displayOrder")
    public List<Category> children;
}
