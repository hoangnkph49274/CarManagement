var express = require('express');
var router = express.Router();

const mongoDb = "mongodb+srv://hoang:kh091205@demo1.xmbo5.mongodb.net/?retryWrites=true&w=majority&appName=demo1"
const mongoose = require('mongoose')
mongoose.connect(mongoDb, {useNewUrlParser: true, useUnifiedTopology: true}).then(() =>{
  console.log("Connected to mongodb")
}).catch(err =>{
  console.log("Error connecting to mongodb")
})

const carsSCHEMA = new mongoose.Schema({
  name: String,
  manufacturer: String,
  year: Number,
  price: Number,
  description: String
})

const XE = mongoose.model("XE", carsSCHEMA)

router.get('/createCar', (req, res) => {
  const cars = new XE({
        name: 'Toyota Corolla',
        manufacturer: 'Toyota',
        year: 2020, price: 28000,
        description: 'Một chiếc sedan nhỏ gọn, tiết kiệm nhiên liệu, thích hợp cho gia đình.' }
    );
  cars.save().then(result => {
    res.send(result);
  })
})

// Trang danh sách xe
router.get('/', function(req, res, next) {
  XE.find({}).then(xe => {
    res.render('index', {title: "Danh sách xe", cars:xe})
  })

});
router.get('/getAllCar', function(req, res) {
    XE.find({}).then(xe => {
        res.send(xe)
    })
})
router.get('/addcar', function(req, res) {
  res.render("form")
})
router.post('/add', (req, res) => {
  const name = req.body.name;
  const manufacturer = req.body.manufacturer;
  const year = req.body.year;
  const price = req.body.price;
  const description = req.body.description;
  const car = new XE({name, manufacturer, year, price, description})
  car.save().then(result => {
    // Chuyển hướng về trang thêm xe
      console.log("Cập nhật thành công!");
      res.status(200).send(result);
  })
      .catch((err) => {
          console.error("Lỗi khi cập nhật:", err);
          res.status(500).send();
      });
})
router.post('/addCar', (req, res) => {
    const name = req.body.name;
    const manufacturer = req.body.manufacturer;
    const year = req.body.year;
    const price = req.body.price;
    const description = req.body.description;
    const car = new XE({name, manufacturer, year, price, description})
    car.save().then(result => {
        // Chuyển hướng về trang thêm xe
        res.redirect('/')
    })
})
router.get('/deleteCar/:id', (req, res) => {
  const id = req.params.id;
  XE.deleteOne({_id: id}).then(() => {
    res.redirect('/');
  })
})
router.get('/delete/:id', (req, res) => {
    const id = req.params.id;
    XE.deleteOne({_id: id}).then(result => {
        res.send(result);
    })
})
router.post('/updateCar/:id', (req, res) => {
  const { id } = req.params; // Lấy ID từ params
  const { name, manufacturer, year, price, description } = req.body; // Dữ liệu từ body

  XE.updateOne(
      { _id: id }, // Truy vấn theo _id (hoặc id nếu bạn dùng trường khác)
      { name, manufacturer, year, price, description } // Dữ liệu cần cập nhật
  )
      .then(() => {
        console.log("Cập nhật thành công!");
        res.redirect('/');
      })
      .catch((err) => {
        console.error("Lỗi khi cập nhật:", err);
        res.status(500).send("Cập nhật thất bại.");
      });
})
router.post('/update/:id', (req, res) => {
    const { id } = req.params; // Lấy ID từ params
    const { name, manufacturer, year, price, description } = req.body; // Dữ liệu từ body

    XE.updateOne(
        { _id: id }, // Truy vấn theo _id (hoặc id nếu bạn dùng trường khác)
        { name, manufacturer, year, price, description } // Dữ liệu cần cập nhật
    )
        .then(result => {
            console.log("Cập nhật thành công!");
            res.send(result);
        })

})
router.get('/updateCar/:id', (req, res) => {
  const {id} = req.params; // Nhận `id` từ query string

  console.log("ID nhận được:", id); // Debug để kiểm tra id

  if (!id) {
    return res.status(400).send("Thiếu ID trong query.");
  }

  XE.findOne({ _id: id })
      .then(car => {
        if (!car) {
          return res.status(404).send("Không tìm thấy xe.");
        }
        // Render trang chỉnh sửa xe
        res.render('edit', { title: 'Chỉnh sửa xe', car });
      })
      .catch(err => res.status(500).send("Lỗi khi tìm xe: " + err.message));
});

module.exports = router;
