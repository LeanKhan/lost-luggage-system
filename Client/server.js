/***
 * SEN306 Group  7 Project.
 * February 4th 2021
 * - Emmanuel Segun-Lean
 * - Maxwell Ogalabu
 * - Shugaba Wuta
 * - Judith Ogoh
 * - Lloyd Ochukenyi
 * School: American University of Nigeria.
 * Instructor: Dr. Ignace Djitog
 */
const express = require("express");
const bodyParser = require("body-parser");
const path = require("path");

const app = express();
const eta = require("eta");

const net = require("net");

process.on("unhandledRejection", (error, promise) => {
  console.log(" Omo! We forgot to handle a promise rejection here: ", promise);
  console.log(" The error was: ", error);
});

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.use(express.static(path.join(__dirname, "public")));

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

const PORT = process.env.PORT || 3000;

const client = net.connect({ port: 5555 }, () => {
  //NOTE: use same port of server!
  console.log("connected to Java server!");
  send(client, "GET", "say-hello");
});

client.setEncoding("utf8");

client.on("data", (data) => {
  // jUst to log all responses from the Java server...
  console.log(data.toString());
});

client.on("end", () => {
  console.log("disconnected from Java server");
});

client.on("error", (err) => {
  console.error("Error launching Client Server => ", err);
});

app.get("/", (req, res) => {
  res.locals.route = "home";
  res.render("index");
});

app.get("/luggages", (req, res) => {
  res.locals.route = "luggages";

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
  res.locals.route = "check-in";

  res.render("check-in-luggage");
});

app.post("/poster", (req, res) => {
  return res.status(200).send(req.body);
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

    res.redirect("/luggages");
  });
});

app.get("/search", (req, res) => {
  res.locals.route = "search";

  res.render("search");
});

app.post("/search", (req, res) => {
  res.locals.route = "search";

  if (!req.body.query) {
    return res.redirect("/search");
  }

  send(client, "POST", "search-luggages", req.body.query);

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    // shorthand for const status = Array[0]...
    const [status, message, data] = p.split(" | ");

    console.log(`Response from Java => ${status} ${message}`);

    let parsed;

    try {
      parsed = JSON.parse(data);
    } catch (err) {
      console.log("Could not parse response :/");
      parsed = data;
    }

    console.log(parsed);

    res.render("search", { data: { luggage: parsed } });
  });
});

app.get("/report", (req, res) => {
  res.locals.route = "report";

  send(client, "GET", "report");

  client.once("data", (response) => {
    const p = response.toString().replace("\r\n", "");

    // shorthand for const status = Array[0]...
    const [status, message, data] = p.split(" | ");

    if (req.query.popout) {
      res.send(`<div style="max-width: 100%">
      <p style="white-space: pre-line">${data}</p>
    </div>`);
      return res.end;
    }

    return res.render("report", { report: data });
  });
});

app.post("/luggage", (req, res) => {
  if (
    !req.body.flight ||
    !req.body.weight ||
    !req.body.owner ||
    !req.body.bags
  ) {
    return res.redirect("check-in-luggage");
  }
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

app.get("/exit", (req, res) => {
  res.send("");
});

app.get("*", (req, res) => {
  res.send("Page does not exist!");
});

app.use((error, req, res, next) => {
  return res
    .status(400)
    .send("Omo! There was an unknown error. Go back <a href='/'>home</a>");
});

app.listen(PORT, () => {
  console.log("Node Server-Client started! Listening on Port => ", PORT);
});
