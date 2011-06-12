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
/*
// Constants for Grace Cathedral lighting
const vec3 L00  = vec3( 0.78908,  0.43710,  0.54161);
const vec3 L1m1 = vec3( 0.39499,  0.34989,  0.60488);
const vec3 L10  = vec3(-0.33974, -0.18236, -0.26940);
const vec3 L11  = vec3(-0.29213, -0.05562,  0.00944);
const vec3 L2m2 = vec3(-0.11141, -0.05090, -0.12231);
const vec3 L2m1 = vec3(-0.26240, -0.22401, -0.47479);
const vec3 L20  = vec3(-0.15570, -0.09471, -0.14733);
const vec3 L21  = vec3( 0.56014,  0.21444,  0.13915);
const vec3 L22  = vec3( 0.21205, -0.05432, -0.30374);
*/
// Constants for Old Town Square lighting
/*
const vec3 L00 = vec3( 0.871297, 0.875222, 0.864470);
const vec3 L1m1 = vec3( 0.175058, 0.245335, 0.312891);
const vec3 L10 = vec3( 0.034675, 0.036107, 0.037362);
const vec3 L11 = vec3(-0.004629, -0.029448, -0.048028);
const vec3 L2m2 = vec3(-0.120535, -0.121160, -0.117507);
const vec3 L2m1 = vec3( 0.003242, 0.003624, 0.007511);
const vec3 L20 = vec3(-0.028667, -0.024926, -0.020998);
const vec3 L21 = vec3(-0.077539, -0.086325, -0.091591);
const vec3 L22 = vec3(-0.161784, -0.191783, -0.219152);
*/
/*
// Constants for Eucalyptus Grove lighting
const vec3 L00  = vec3( 0.3783264,  0.4260425,  0.4504587);
const vec3 L1m1 = vec3( 0.2887813,  0.3586803,  0.4147053);
const vec3 L10  = vec3( 0.0379030,  0.0295216,  0.0098567);
const vec3 L11  = vec3(-0.1033028, -0.1031690, -0.0884924);
const vec3 L2m2 = vec3(-0.0621750, -0.0554432, -0.0396779);
const vec3 L2m1 = vec3( 0.0077820, -0.0148312, -0.0471301);
const vec3 L20  = vec3(-0.0935561, -0.1254260, -0.1525629);
const vec3 L21  = vec3(-0.0572703, -0.0502192, -0.0363410);
const vec3 L22  = vec3( 0.0203348, -0.0044201, -0.0452180);
*/
/*
// Constants for St. Peter's Basilica lighting
const vec3 L00  = vec3( 0.3623915,  0.2624130,  0.2326261);
const vec3 L1m1 = vec3( 0.1759130,  0.1436267,  0.1260569);
const vec3 L10  = vec3(-0.0247311, -0.0101253, -0.0010745);
const vec3 L11  = vec3( 0.0346500,  0.0223184,  0.0101350);
const vec3 L2m2 = vec3( 0.0198140,  0.0144073,  0.0043987);
const vec3 L2m1 = vec3(-0.0469596, -0.0254485, -0.0117786);
const vec3 L20  = vec3(-0.0898667, -0.0760911, -0.0740964);
const vec3 L21  = vec3( 0.0050194,  0.0038841,  0.0001374);
const vec3 L22  = vec3(-0.0818750, -0.0321501,  0.0033399);
*/
/*
// Constants for Campus Sunset lighting
const vec3 L00  = vec3( 0.7870665,  0.9379944,  0.9799986);
const vec3 L1m1 = vec3( 0.4376419,  0.5579443,  0.7024107);
const vec3 L10  = vec3(-0.1020717, -0.1824865, -0.2749662);
const vec3 L11  = vec3( 0.4543814,  0.3750162,  0.1968642);
const vec3 L2m2 = vec3( 0.1841687,  0.1396696,  0.0491580);
const vec3 L2m1 = vec3(-0.1417495, -0.2186370, -0.3132702);
const vec3 L20  = vec3(-0.3890121, -0.4033574, -0.3639718);
const vec3 L21  = vec3( 0.0872238,  0.0744587,  0.0353051);
const vec3 L22  = vec3( 0.6662600,  0.6706794,  0.5246173);
*/

uniform vec3 L00;
uniform vec3 L1m1;
uniform vec3 L10;
uniform vec3 L11;
uniform vec3 L2m2;
uniform vec3 L2m1;
uniform vec3 L20;
uniform vec3 L21;
uniform vec3 L22;



void main()
{
	// spherical harmonics lighting
	vec3 tnorm = normalize(gl_NormalMatrix * gl_Normal);
	DiffuseColor = C1 * L22 * (tnorm.x * tnorm.x - tnorm.y * tnorm.y) +
	C3 * L20 * tnorm.z * tnorm.z +
	C4 * L00 -
	C5 * L20 +
	2.0 * C1 * L2m2 * tnorm.x * tnorm.y +
	2.0 * C1 * L21 * tnorm.x * tnorm.z +
	2.0 * C1 * L2m1 * tnorm.y * tnorm.z +
	2.0 * C2 * L11 * tnorm.x +
	2.0 * C2 * L1m1 * tnorm.y +
	2.0 * C2 * L10 * tnorm.z;
	DiffuseColor *= ScaleFactor;

	// environment mapping
	Normal = normalize(gl_NormalMatrix * gl_Normal);
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	Pos = gl_Vertex;
	EyeDir = pos.xyz;
	LightIntensity = max(dot(normalize(LightPos - EyeDir), Normal), 0.0);

	// specular light
	vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 lightVec = normalize(LightPos - ecPosition);
	vec3 reflectVec = reflect(-lightVec, Normal);
	vec3 viewVec = normalize(-ecPosition);
	float spec = clamp(dot(reflectVec, viewVec), 0.0, 1.0);
	spec = pow(spec, 32.0);
	specularIntensity = diffuseContribution * max(dot(lightVec, Normal), 0.0) + specularContribution * spec;

	gl_Position = ftransform();

	float myDepthRatio = gl_Position.z / far;
	depthIntensity = myDepthRatio;// vec4(myDepthRatio, myDepthRatio, myDepthRatio, 1.0);

	// texture coordinates
	gl_TexCoord[0] = gl_MultiTexCoord0;
}