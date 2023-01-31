import 'package:flutter/material.dart';
import 'api.dart';

class CategoriesPage extends StatefulWidget {
  const CategoriesPage({super.key});

  @override
  // ignore: library_private_types_in_public_api
  _CategoriesPageState createState() => _CategoriesPageState();
}

class _CategoriesPageState extends State<CategoriesPage> {
  List<Category> categories = [];

  @override
  void initState() {
    super.initState();
    API.loadCategories(() {
      setState(() {
        categories = API.getCategories();
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Categories'),
      ),
      body: ListView.builder(
        itemCount: categories.length,
        itemBuilder: (context, index) {
          return ListTile(title: Text(categories[index].name));
        },
      ),
    );
  }
}
