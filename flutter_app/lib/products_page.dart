import 'package:flutter/material.dart';
import 'api.dart';

class ProductsPage extends StatefulWidget {
  const ProductsPage({super.key});

  @override
  // ignore: library_private_types_in_public_api
  _ProductsPageState createState() => _ProductsPageState();
}

class _ProductsPageState extends State<ProductsPage> {
  List<ProductWithCategory> products = [];

  @override
  void initState() {
    super.initState();
    // load token from api using future and then set state
    API.loadProducts(() {
      setState(() {
        products = API.getProducts();
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Products')),
      body: ListView.builder(
        itemCount: products.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text('${products[index].name} - ${products[index].price}'),
            subtitle: Text(products[index].categoryName),
          );
        },
      ),
    );
  }
}
