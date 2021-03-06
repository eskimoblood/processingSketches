varying vec3 DiffuseColor;

const vec3 Xunitvec = vec3(1.0, 0.0, 0.0);
const vec3 Yunitvec = vec3(0.0, 1.0, 0.0);
uniform vec3 BaseColor;
uniform float MixRatio;
uniform sampler2D EnvMap;
varying vec3 Normal;
varying vec3 EyeDir;
varying float LightIntensity;
varying float depthIntensity;
varying float specularIntensity;
varying vec4 Pos;
uniform sampler2D Noise;

uniform float widthX;
uniform float widthY;
uniform float widthZ;
uniform float widthQ;
uniform float widthW;
uniform float widthL;

float Scale = 1.0;

float find_closest(int x, int  y, float c0)
{
 mat4 dither = mat4(
                         1.0, 33.0,  9.0, 41.0,
                        49.0, 17.0, 57.0, 25.0,
                        13.0, 45.0,  5.0, 37.0,
                        61.0, 29.0, 53.0, 21.0 );

 float limit = 0.0;
 if(x < 4)
 {
  limit = (dither[x][y]+1.0)/64.0;
 }


 if(c0 < limit)
  return 0.0;
 return 1.0;
}

void main()
{
	// Compute reflection vector
	vec3 reflectDir = reflect(EyeDir, Normal);
	// Compute altitude and azimuth angles
	vec2 index;
	index.t = dot(normalize(reflectDir), Yunitvec);
	reflectDir.y = 0.0;
	index.s = dot(normalize(reflectDir), Xunitvec) * 0.5;
	// Translate index values into proper range
	if (reflectDir.z >= 0.0)
		index = (index + 1.0) * 0.5;
	else
	{
		index.t = (index.t + 1.0) * 0.5;
		index.s = (-index.s) * 0.5 + 1.0;
	}
	// if reflectDir.z >= 0.0, s will go from 0.25 to 0.75
	// if reflectDir.z < 0.0, s will go from 0.75 to 1.25, and
	// that's OK, because we've set the texture to wrap.

	// Do a lookup into the environment map.
	vec3 envColor = vec3(texture2D(EnvMap, sin(index*widthL)));

	// Add lighting to base color and mix
	vec3 base = LightIntensity * BaseColor* sin(fract(Pos.x*widthL ));
	envColor = vec3(.95, .95, .95);// mix(envColor, base, MixRatio);

	// spherical harmonics lighting
	// if(gl_FragCoord.x < 420.0)
	envColor *= DiffuseColor;

	// make depth to dust
	envColor *= 1.0 - depthIntensity;
	// envColor = vec3(depthIntensity, depthIntensity, depthIntensity);

	// add specular
	// if(gl_FragCoord.x < 420.0)
		envColor *= .5 + specularIntensity*.5;// * 2.5;

	// gl_FragColor = vec4(envColor, 1.0);

vec3 color = envColor;// texture2D(bgl_RenderedTexture, gl_TexCoord[0].xy).rgb;
 vec3 luminosity = vec3(0.30, 0.59, 0.11);
 float lum = dot(luminosity, color);

 float v = gl_FragCoord.s;
 float h = gl_FragCoord.t;

 gl_FragColor = vec4(envColor, 1.0);


float sawtooth = fract(cos(Pos.x*widthX ) + sin(Pos.y*widthY ))*widthL;
float triangle = (abs((widthQ * sawtooth) -widthW));
float square = smoothstep(.2, .8, (triangle));
color = mix(DiffuseColor.brg,BaseColor.rgb, square);
gl_FragColor.rgb *= color.rgb ;

 sawtooth = fract(cos(Pos.y*widthY ) + sin(Pos.x*widthX ))*widthZ;
 triangle = (abs((widthQ * sawtooth) -widthW));
 square = smoothstep(.2, .8, (triangle));
color = mix(DiffuseColor.rgb,BaseColor.rgb, square);
gl_FragColor.rgb *= color.brg ;



}