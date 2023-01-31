import 'dart:convert';
import 'package:http/http.dart' as http;

const String url = 'http://192.168.0.129:5000';

class API {
  static String token = '';
  static List<Product> products = [];
  static List<Category> categories = [];
  static bool prod = false;
  static bool cat = false;

  static void loadToken(Function next) async {
    var requestUrl = Uri.parse('$url/auth/login');
    var response = await http.post(requestUrl,
        headers: {'Content-Type': 'application/json'},
        body: '{"email":"user@example.com","password":"user"}');
    if (response.statusCode == 200) {
      token = response.body;
    }
    next();
  }

  static void loadCategories(Function next) async {
    if (cat) {
      next();
    } else if (token == '') {
      loadToken(() => loadCategories(next));
    } else {
      var requestUrl = Uri.parse('$url/categories');
      var response = await http
          .get(requestUrl, headers: {'Authorization': 'Bearer $token'});
      if (response.statusCode == 200) {
        categories = jsonDecode(response.body)
            .cast<Map<String, dynamic>>()
            .map<Category>((json) => Category.fromJson(json))
            .toList();
        cat = true;
      }
      next();
    }
  }

  static void loadProducts(Function next) async {
    if (prod) {
      next();
    } else if (!cat) {
      loadCategories(() => loadProducts(next));
    } else {
      var requestUrl = Uri.parse('$url/products');
      var response = await http
          .get(requestUrl, headers: {'Authorization': 'Bearer $token'});
      if (response.statusCode == 200) {
        products = jsonDecode(response.body)
            .cast<Map<String, dynamic>>()
            .map<Product>((json) => Product.fromJson(json))
            .toList();
        prod = true;
      }
      next();
    }
  }

  static List<Category> getCategories() {
    return categories;
  }

  static List<ProductWithCategory> getProducts() {
    return products.map((product) {
      var category = categories
          .firstWhere((category) => category.categoryId == product.categoryId);
      return ProductWithCategory(product: product, category: category);
    }).toList();
  }
}

class Product {
  final int productId;
  final String name;
  final int categoryId;
  final int price;
  final int inStock;
  final String description;

  const Product({
    required this.productId,
    required this.name,
    required this.categoryId,
    required this.price,
    required this.inStock,
    required this.description,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      productId: json['productId'],
      name: json['name'],
      categoryId: json['categoryId'],
      price: json['price'],
      inStock: json['inStock'],
      description: json['description'],
    );
  }
}

class Category {
  final int categoryId;
  final String name;

  const Category({
    required this.categoryId,
    required this.name,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      categoryId: json['categoryId'],
      name: json['name'],
    );
  }
}

class ProductWithCategory {
  int productId = 0;
  String name = '';
  String categoryName = '';
  String price = '0.00\$';
  int inStock = 0;
  String description = '';

  ProductWithCategory({required Product product, required Category category}) {
    productId = product.productId;
    name = product.name;
    categoryName = category.name;
    price = '${product.price.toDouble() / 100.0}\$';
    inStock = product.inStock;
    description = product.description;
  }
}
