
uniform float BaseRadius;
uniform sampler2D permTexture;	
uniform float duration;
const float PI = 3.14159265;
const float TWOPI = 6.28318531;


////////////////////////////////
// Calculate sphere           //
////////////////////////////////
vec4 sphere(in float u, in float v, float radius) {

	v *= PI;
	u *= TWOPI;

	vec4 pSphere;

	pSphere.x = radius * sin(v) * cos(u);
    pSphere.y = radius * sin(v) * sin(u);
    pSphere.z = radius * cos(v);
    pSphere.w = 1.0;
    return pSphere;
	//return vec4(u*BaseRadius,v*BaseRadius,radius,1.0);
}


vec4 moebius(in float u, in float v, float radius) {

	v = (v * 3.0) -1.5;
	u *= TWOPI;

	vec4 pSphere;

	//pSphere.x = radius * cos(s) + t * cos(s / 2.0) * cos(s);
    //pSphere.y = radius * sin(s) + t * cos(s / 2.0) * sin(s);
    //pSphere.z = radius * t * sin(s / 2.0);

    pSphere.x = radius * (1.0+ .5*v * cos(.5*u))*cos(u);
    pSphere.y = radius * (1.0+ .5*v * cos(.5*u))*sin(u);
    pSphere.z = radius * .5*v*sin(.5*u);
    pSphere.w = 1.0;
	return pSphere;
}

vec4 texture2D_bilinear(  sampler2D tex, vec2 uv )
{
	vec2 f = fract( uv.xy * 514.0 );
	vec4 t00 = texture2D( tex, uv );
	vec4 t10 = texture2D( tex, uv + vec2( 1.0, 0.0 ));
	vec4 tA = mix( t00, t10, f.x );
	vec4 t01 = texture2D( tex, uv + vec2( 0.0, 1.0 ) );
	vec4 t11 = texture2D( tex, uv + vec2( 1.0, 1.0 ) );
	vec4 tB = mix( t01, t11, f.x );
	return mix( tA, tB, f.y );
}
float radius (in float u, in float v) {

    return BaseRadius + texture2D_bilinear(permTexture, vec2(u,v)).x * duration;

}


////////////////////////////////
// Calculate Position, Normal //
////////////////////////////////
const float grid = 0.01;	// Grid offset for normal-estimation
varying vec3 norm;
vec4 position;

void posNorm(in float u, in float v) {


	position = sphere(u, v, radius(u,v));
    vec3 tangent = (sphere(u + grid, v, radius(u + grid, v))  - position).xyz;
	vec3 bitangent = (sphere(u, v + grid, radius(u, v + grid)) - position).xyz;

	//position = moebius(u, v, radius(u,v));
  	//vec3 tangent = (moebius(u + grid, v, radius(u + grid, v))  - position).xyz;
	//vec3 bitangent = (moebius(u, v + grid, radius(u, v + grid)) - position).xyz;
	
	norm  = gl_NormalMatrix * normalize(cross(tangent, bitangent));

}

varying vec3 lightDir0, halfVector0;
varying vec4 diffuse0, ambient;

void phongDir_VS() {
	// Extract values from gl light parameters
	// and set varyings for Fragment Shader
	lightDir0 = normalize(vec3(gl_LightSource[0].position));
	halfVector0 = normalize(gl_LightSource[0].halfVector.xyz);
	diffuse0 = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
	ambient =  gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
	ambient += gl_LightModel.ambient * gl_FrontMaterial.ambient;
}



uniform float far;
uniform float ScaleFactor;
uniform vec3 LightPos;

varying vec3 DiffuseColor;

varying vec3 Normal;
varying vec4 Pos;
varying vec3 EyeDir;
varying float LightIntensity;
varying float depthIntensity;
varying float specularIntensity;

const float specularContribution = 0.25;
const float diffuseContribution = 1.0 - specularContribution;

const float C1 = 0.429043;
const float C2 = 0.511664;
const float C3 = 0.743125;
const float C4 = 0.886227;
const float C5 = 0.247708;
uniform vec3 L00;
uniform vec3 L1m1;
uniform vec3 L10;
uniform vec3 L11;
uniform vec3 L2m2;
uniform vec3 L2m1;
uniform vec3 L20;
uniform vec3 L21;
uniform vec3 L22;
 varying vec2 uv;

///////////////
// Main Loop //
///////////////
void main() {

	float u = gl_Vertex.x;
	float v = gl_Vertex.y;
     uv = gl_Vertex.xy;
	posNorm(u, v);

    //phongDir_VS();

    // spherical harmonics lighting

	DiffuseColor = C1 * L22 * (norm.x * norm.x - norm.y * norm.y) +
	C3 * L20 * norm.z * norm.z +
	C4 * L00 -
	C5 * L20 +
	2.0 * C1 * L2m2 * norm.x * norm.y +
	2.0 * C1 * L21 * norm.x * norm.z +
	2.0 * C1 * L2m1 * norm.y * norm.z +
	2.0 * C2 * L11 * norm.x +
	2.0 * C2 * L1m1 * norm.y +
	2.0 * C2 * L10 * norm.z;
	DiffuseColor *= ScaleFactor;

	// environment mapping
	vec4 pos = gl_ModelViewMatrix * position;
	Pos = position;
	EyeDir = pos.xyz;
	LightIntensity = max(dot(normalize(LightPos - EyeDir), norm), 0.0);

	// specular light
	vec3 ecPosition = vec3(gl_ModelViewMatrix * position);
	vec3 lightVec = normalize(LightPos - ecPosition);
	vec3 reflectVec = reflect(-lightVec, norm);
	vec3 viewVec = normalize(-ecPosition);
	float spec = clamp(dot(reflectVec, viewVec), 0.0, 1.0);
	spec = pow(spec, 32.0);
	specularIntensity = diffuseContribution * max(dot(lightVec, norm), 0.0) + specularContribution * spec;


	float myDepthRatio = position.z / far;
	depthIntensity = myDepthRatio;// vec4(myDepthRatio, myDepthRatio, myDepthRatio, 1.0);

	// texture coordinates


 	gl_Position = gl_ModelViewProjectionMatrix * position;
    gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;


}

