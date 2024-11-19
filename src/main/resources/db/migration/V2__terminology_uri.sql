
-- update uri.suomi.fi -> iri.suomi.fi
UPDATE resource SET uri=REPLACE(uri, 'http://uri.suomi.fi/terminology', 'https://iri.suomi.fi/terminology');
UPDATE user_resource SET resource_uri=REPLACE(resource_uri, 'http://uri.suomi.fi/terminology', 'https://iri.suomi.fi/terminology');

-- add traling slash if missing
UPDATE resource SET uri=CONCAT(uri, '/') WHERE uri LIKE '%/terminology/%' AND uri NOT LIKE '%/';
UPDATE user_resource SET resource_uri=CONCAT(resource_uri, '/') WHERE resource_uri LIKE '%/terminology/%' AND resource_uri NOT LIKE '%/';