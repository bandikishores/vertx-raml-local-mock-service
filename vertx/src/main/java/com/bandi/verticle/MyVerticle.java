package com.bandi.verticle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.yaml.snakeyaml.Yaml;

import com.bandi.data.ResponseData;
import com.bandi.http.HttpRequestResponseHandler;
import com.bandi.log.Logger;
import com.bandi.raml.RAMLParser;
import com.bandi.util.Constants;
import com.bandi.util.Utils;
import com.bandi.validate.Validator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

public class MyVerticle extends AbstractVerticle {

	private HttpServer httpServer = null;

	private HashMap<String, ResponseData> cacheofRAML = new HashMap<String, ResponseData>();

	@Override
	public void start(Future<Void> startFuture) {
		Logger.log("MyVerticle started!");

		httpServer = vertx.createHttpServer();

		processRAML();

		httpServer.requestHandler(new HttpRequestResponseHandler(cacheofRAML));

		httpServer.listen(Constants.PORT);
	}

	private void processRAML() {
		List<Path> pathToFiles = Utils.getRAMLFilesPath();

		if (CollectionUtils.isNotEmpty(pathToFiles)) {
			for (Path path : pathToFiles) {
				String ramlLocation = path.toUri().toString();

				if (Validator.isValidRAML(ramlLocation)) {
					Raml raml = new RamlDocumentBuilder().build(ramlLocation);

					if (raml != null) {
						RAMLParser ramlParser = new RAMLParser(cacheofRAML);
						ramlParser.parse(raml);
					} else {
						Logger.log(" Documentation not present for RAML to load example");
					}
				} else {
					Logger.log("Couldn't load raml at " + ramlLocation);
				}
			}
		} else {
			Logger.log("No RAMLs found");
		}
	}

	@Override
	public void stop(Future stopFuture) throws Exception {
		Logger.log("MyVerticle stopped!");
	}

	private TagResolver[] defaultResolver(TagResolver[] tagResolvers) {
		TagResolver[] defaultResolvers = new TagResolver[] { new IncludeResolver(), new JacksonTagResolver(),
				new JaxbTagResolver() };
		return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
	}

	private void yamlParserForExtractingExample(URL url, String ramlLocation, Resource resource) {
		Yaml yaml = (Yaml) new YamlDocumentBuilder(Yaml.class, new DefaultResourceLoader(), defaultResolver(null))
				.build(ramlLocation);

		FileReader fr;
		try {
			fr = new FileReader(url.getFile());

			Map config = (Map) yaml.load(fr);
			Map usersConfig = ((Map) config.get(resource.getUri()));
		} catch (FileNotFoundException e) {
			Logger.log(e);
		}
	}

}
