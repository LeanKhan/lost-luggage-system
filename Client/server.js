const express = require("express");
const bodyParser = require("body-parser");

const app = express();
const eta = require("eta");

const net = require("net");

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

eta.configure({ useWith: true });

app.engine("eta", eta.renderFile);

app.set("view engine", "eta");

app.set("views", "./views");

function createRequest(method, message, data) {
  return `${method} | ${message} | ${data}\n`;
}

function send(client, method, message, data = "-") {
  client.write(createRequest(method, message, data));
}

const client = net.connect({ port: 5555 }, () => {
  //NOTE: use same port of server!
  console.log("connected to server!");
  send(client, "GET", "message");
});

client.setEncoding("utf8");

client.on("data", (data) => {
  // jUst to log all responses from the Java server...
  console.log(data.toString());
});

client.on("end", () => {
  console.log("disconnected from java server");
});

client.on("error", (err) => {
  console.error("Error launching Client Server => ", err);
});

app.get("/", (req, res) => {
  res.render("index");
});

app.get("/luggages", (req, res) => {
  send(client, "GET", "luggages");

  client.once("data", (response) => {
    const ls = response.toString().replace("\r\n", "");

    // shorthand for const status = Array[0]...
    const [status, message, data] = ls.split(" | ");

    let parsed;

    try {
      parsed = JSON.parse(data);
    } catch (err) {
      console.log("Could not parse response :/");
      parsed = data;
    }

    console.log(parsed);

    res.render("luggages", { data: parsed });
  });
});

app.get("/check-in-luggage", (req, res) => {
  res.render("check-in-luggage");
});

app.post("/checkout-luggage", (req, res) => {
  console.log(req.body);

  send(client, "POST", "checkout-luggage", req.body.luggage_id);

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    // shorthand for const status = Array[0]...
    const [status, message, data] = p.split(" | ");

    let parsed;

    try {
      parsed = JSON.parse(data);
    } catch (err) {
      console.log("Could not parse response :/");
      parsed = data;
    }

    console.log(parsed);

    res.render("index");
  });
});

app.get("/search", (req, res) => {
  res.render("search");
});

app.get("/report", (req, res) => {
  res.render("report");
});

app.get("/person", (req, res) => {
  // We are sending a request to the Java server for a person...
  send(client, "GET", "person");

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    // shorthand for const status = Array[0]...
    const [status, message, data] = p.split(" | ");

    let parsed;

    try {
      parsed = JSON.parse(data);
    } catch (err) {
      console.log("Could not parse response :/");
      parsed = data;
    }

    console.log(parsed);

    res.render("person", { person: parsed });
  });
});

app.post("/person", (req, res) => {
  // We stringify the form data
  const p = JSON.stringify(req.body);

  // We are sending a sending a POST request to the Java server.Java
  // to create a new Person Object.
  send(client, "POST", "person", p);

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    const [status, message, data] = p.split(" | ");

    console.log(`Response from Java => ${status} ${message} ${data}`);

    // go back to the Person page so that we can see the just created Person.
    res.redirect("/person");
  });
});

app.post("/luggage", (req, res) => {
  // We stringify the form data
  const l = JSON.stringify(req.body);

  // We are sending a sending a POST request to the Java server.Java
  // to create a new Person Object.
  send(client, "POST", "luggage", l);

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    const [status, message, data] = p.split(" | ");

    console.log(`Response from Java => ${status} ${message} ${data}`);

    // go back to the Person page so that we can see the just created Person.
    res.redirect("luggages");
  });
});

app.listen(process.env.PORT || 3000, () => {
  console.log("Node Server-Client started!");
});
